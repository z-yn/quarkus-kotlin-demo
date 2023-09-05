package example.quarkus.mutiny

import io.smallrye.mutiny.Uni
import java.time.Duration

//Uni<T>代表一个只能发出一个元素或一个失败事件的流
//Uni<T>非常适合表示异步操作，例如远程过程调用、HTTP请求或产生单个结果的操作。
fun buildUni(): Uni<String> {
    return Uni.createFrom().item(1)
        .onItem().transform { i: Int -> "hello-$i" }
        .onItem().delayIt().by(Duration.ofMillis(100))
}



