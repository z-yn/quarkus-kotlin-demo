package example.quarkus.mutiny

import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.TimeoutException
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.groups.GeneratorEmitter
import org.junit.jupiter.api.Test
import java.time.Duration
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
        Multi.createFrom().items(1, 2, 3, 4)
            .assertIs(1, 2, 3, 4)
        //从列表创建
        Multi.createFrom().iterable(listOf(1, 2, 3, 4))
            .assertIs(1, 2, 3, 4)

        //从supplier创建
        Multi.createFrom()
            .items {
                IntStream.range(0, 100).boxed()
            }.assertStart(0, 1, 2, 3, 4)

        Multi.createFrom()
            .range(0, 100)
            .assertStart(0, 1, 2, 3, 4)

        //创建一个抛出异常的流
        Multi.createFrom().failure<Int>(MutinyBuildException)
            .assertThat().assertFailedWith(MutinyBuildException::class.java)

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
        Multi.createFrom().generator(
            { 1 }
        ) { n: Int, emitter: GeneratorEmitter<in Any?> ->
            val next = n + n / 2 + 1
            if (n < 50) {
                emitter.emit(next)
            } else {
                emitter.complete()
            }
            next
        }.assertIs(2, 4, 7, 11, 17, 26, 40, 61)

        //支持定期产生
        Multi.createFrom()
            .ticks()
            .every(Duration.ofMillis(100))
            .assertStart(0, 1, 2, 3, 4)
    }

    @Test
    fun testTransform() {

        Multi.createFrom().items("","","a", "b" ,"", "c")
            .skip().first { it.isBlank() } //从第一个不是空的开始
            .assertIs("a","b","","c")


        Multi.createFrom().items("a", "b" ,"","c")
            .skip().first(3) //过滤掉前三个
            .assertIs("c")

        Multi.createFrom().items("a", "b" ,"","c")
            .skip().last(3) //过滤掉后三个
            .assertIs("a")
        Multi.createFrom().items("a", "a" ,"b","b","b", "a", "c")
            .skip().repetitions()//去除连续重复
            .assertIs("a","b","a","c")

        Multi.createFrom().items("a", "a" ,"b","b","b", "a", "c")
            .select().distinct()//整体去重复
            .assertIs("a","b","c")

        Multi.createFrom().items("a", "b" ,"","c")
            .select().`when` { Uni.createFrom().item { it=="a"||it=="b" } } //when提供异步版本返回的是Uni
            .select().where { it=="a"||it=="b" } //where提供同步版本判断
            .filter { it=="a" } //filter是select.select().where的快捷操作符
            .assertIs("a")

        Multi.createFrom().items("a", "b" ,"c")
            .onItem().transform { it.uppercase() } //提供同步版本的转换
            .map { "Hello-$it" } //map is shortcut for .onItem().transform {}
            .assertIs("Hello-A","Hello-B","Hello-C")

    }

    @Test
    fun testTimeout() {
        Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .ifNoItem().after(Duration.ofMillis(100)) //超时时间
            .failWith(TimeoutException()) //抛出异常
            .assertThat().awaitFailure()
            .assertFailedWith(TimeoutException::class.java)

        Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .ifNoItem().after(Duration.ofMillis(100)) //超时时间
            .recoverWithMulti(Multi.createFrom().items(100,200)) //相当于fallback
            .assertIs(0,100,200)
    }


}