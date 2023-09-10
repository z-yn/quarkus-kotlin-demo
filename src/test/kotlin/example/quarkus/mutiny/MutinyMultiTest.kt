package example.quarkus.mutiny

import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.TimeoutException
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.groups.GeneratorEmitter
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.IntStream


/**
 * Multi代表一个数据流。流可以发出0、1、n或无限数量的项。
 * 您很少自己创建Multi实例，而是使用一个公开Mutiny API的响应式客户端。
 * 不过，就像Uni一样，有一个丰富的API用于创建多对象。”
 */
@QuarkusTest
class MutinyMultiTest {
    @Test
    fun testMultiBuilder() {
        //从varargs创建
        Multi.createFrom().items(1, 2, 3, 4).assertIs(1, 2, 3, 4)
        //从列表创建
        Multi.createFrom().iterable(listOf(1, 2, 3, 4)).assertIs(1, 2, 3, 4)

        //从supplier创建
        Multi.createFrom().items {
            IntStream.range(0, 100).boxed()
        }.assertStart(0, 1, 2, 3, 4)

        Multi.createFrom().range(0, 100).assertStart(0, 1, 2, 3, 4)

        //创建一个抛出异常的流
        Multi.createFrom().failure<Int>(MutinyBuildException).assertThat()
            .assertFailedWith(MutinyBuildException::class.java)

        //与Uni不同，Multi流不会发送空项(这在响应流中是被禁止的)。
        // 相反，多流发送完成事件，表明没有更多的项目要消耗。
        // 当然，即使没有项目，完成事件也会发生，从而创建一个空流
        Multi.createFrom().empty<Int>().assertEmpty();

        //支持 emitter
        Multi.createFrom().emitter {
            it.emit(1)
            it.emit(2)
            it.emit(3)
            it.complete()
        }.assertIs(1, 2, 3)

        //支持状态机生成器生成
        Multi.createFrom().generator({ 1 }) { n: Int, emitter: GeneratorEmitter<in Any?> ->
            val next = n + n / 2 + 1
            if (n < 50) {
                emitter.emit(next)
            } else {
                emitter.complete()
            }
            next
        }.assertIs(2, 4, 7, 11, 17, 26, 40, 61)

        //支持定期产生
        Multi.createFrom().ticks().every(Duration.ofMillis(100)).assertStart(0, 1, 2, 3, 4)
    }

    @Test
    fun testTransform() {

        Multi.createFrom().items("", "", "a", "b", "", "c").skip().first { it.isBlank() } //从第一个不是空的开始
            .assertIs("a", "b", "", "c")


        Multi.createFrom().items("a", "b", "", "c").skip().first(3) //过滤掉前三个
            .assertIs("c")

        Multi.createFrom().items("a", "b", "", "c").skip().last(3) //过滤掉后三个
            .assertIs("a")
        Multi.createFrom().items("a", "a", "b", "b", "b", "a", "c").skip().repetitions()//去除连续重复
            .assertIs("a", "b", "a", "c")

        Multi.createFrom().items("a", "a", "b", "b", "b", "a", "c").select().distinct()//整体去重复
            .assertIs("a", "b", "c")

        Multi.createFrom().items("a", "b", "", "c").select()
            .`when` { Uni.createFrom().item { it == "a" || it == "b" } } //when提供异步版本返回的是Uni
            .select().where { it == "a" || it == "b" } //where提供同步版本判断
            .filter { it == "a" } //filter是select.select().where的快捷操作符
            .assertIs("a")

        Multi.createFrom().items("a", "b", "c").onItem().transform { it.uppercase() } //提供同步版本的转换
            .map { "Hello-$it" } //map is shortcut for .onItem().transform {}
            .assertIs("Hello-A", "Hello-B", "Hello-C")

        Multi.createFrom().items("a", "b", "c").onItem().transformToUniAndMerge {
            Uni.createFrom().item(it.uppercase())
        }.map { "Hello-$it" } //map is shortcut for .onItem().transform {}
            .assertIs("Hello-A", "Hello-B", "Hello-C")
    }

    @Test
    fun testTimeout() {
        Multi.createFrom().ticks().every(Duration.ofSeconds(1)).ifNoItem().after(Duration.ofMillis(100)) //超时时间
            .failWith(TimeoutException()) //抛出异常
            .assertFail().assertFailedWith(TimeoutException::class.java)

        Multi.createFrom().ticks().every(Duration.ofSeconds(1)).ifNoItem().after(Duration.ofMillis(100)) //超时时间
            .recoverWithMulti(Multi.createFrom().items(100, 200)) //相当于fallback
            .assertIs(0, 100, 200)
    }

    @Test
    fun testDelay() {

        //可以通过onItem.call的方式进行一个同步的Uni延迟
        Multi.createFrom().items(1, 2, 3).onItem().call { it -> uniDelay(20L * it) }.ifNoItem()
            .after(Duration.ofMillis(30)) //超时时间
            .recoverWithMulti(Multi.createFrom().items(100, 200)) //相当于fallback
            .assertIs(1, 100, 200)

        //也可以通过另外一个合并流的同步实现延迟。
        val ticks = Multi.createFrom().ticks().every(Duration.ofSeconds(1)).onOverflow().drop()
        val multi = Multi.createFrom().items("1", "2", "3")
        Multi.createBy().combining().streams(ticks, multi).using { x, item -> "$item-$x" }.ifNoItem()
            .after(Duration.ofMillis(800)) //超时时间
            .fail().assertFail().assertFailedWith(TimeoutException::class.java)
    }

    @Test
    fun testPaginated() {
        val api = PaginatedApi()
        //分页转换，使用repeating获取
        Multi.createBy().repeating()
            //使用Uni
            .uni({ AtomicInteger() }) { state -> api.getPageUni(state.getAndIncrement()) }
            .whilst { it.hasNext() }//直到判断结果为false
            .assertIs(
                Page(0, "0"),
                Page(1, "1"),
                Page(2, "2"),
                Page(3, "3"),
                Page(4, "4"),
                Page(5, "5"),
            )

        //从completionStage创建
        Multi.createBy().repeating()
            .completionStage({ AtomicInteger() }) { state: AtomicInteger -> api.getPage(state.getAndIncrement()) }
            .until { it.isEmpty() }//直到判断条件为true
            .onItem().disjoint<String>().assertIs(
                "0", "1", "2", "3", "4", "5"
            )

        //使用atMost设置最大次数
        Multi.createBy().repeating()
            .completionStage({ AtomicInteger() }) { state: AtomicInteger -> api.getPage(state.getAndIncrement()) }
            .atMost(4) //最多执行次数
            .onItem().disjoint<String>()//disjoint
            .assertIs("0", "1", "2", "3")
    }

    @Test
    fun testPolling() {
        val source1 = PollableDataSource()
        Uni.createFrom().item(source1::poll).repeat().indefinitely().assertStart("polled-0", "polled-1", "polled-2")
            .cancel()

        val source2 = PollableDataSource()
        Multi.createBy().repeating()
            .supplier { source2.poll() }.until { it == "polled-3" }//Until不包含
            .assertIs("polled-0", "polled-1", "polled-2")
    }

    /*
    上下文对象是线程安全的，可以从键/值对序列中创建(如上所示)，也可以从Java Map中创建，或者可以将它们创建为空。
    请注意，空创建的上下文将其内部存储分配延迟到第一次调用put。
    你可以把Context看作是一个美化的ConcurrentHashMap委托，
    尽管这是一个实现细节，Mutiny将来可能会探索各种内部存储策略。
     */
    @Test
    fun testContextPass() {
        //Emitter使用context
        Multi.createFrom().emitter { emitter ->
            val emitterCxt = emitter.context()
            emitter.emit(emitterCxt["key1"])
            emitter.emit(emitterCxt["key2"])
            emitter.complete()
        }.withCtx {
            put("key1", "value1")
            put("key2", "value2")
        }.assertIs("value1", "value2")

        //创建时，使用context
        Multi.createFrom().context { ctx ->
            val prefix: String = ctx["prefix"]
            Multi.createFrom().items(1, 2, 3).map { "$prefix-$it" }
        }.withCtx {
            put("prefix", "context")
        }.assertIs("context-1", "context-2", "context-3")

        //使用withContext来对multi转换中使用context
        Multi.createFrom().items(1, 2, 3).withContext { multi, ctx ->
            multi.map {
                val prefix: String = ctx["prefix"]
                "${prefix}-$it"
            }
        }.withCtx {
            put("prefix", "context")
        }.assertIs("context-1", "context-2", "context-3")

        //使用attachContext来讲context和item合并处理
        Multi.createFrom().items(1, 2, 3).attachContext().map {
            val prefix: String = it.context()["prefix"]
            "${prefix}-${it.get()}"
        }.withCtx {
            put("prefix", "context")
        }.assertIs("context-1", "context-2", "context-3")

    }

}