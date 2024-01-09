package s4y.demo.mapsdksdemo.kotlin.channel

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class ReceiveAsFlowTest {
    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun chanel_shouldEmitToFlow() = runBlocking {
        // Arrange
        val received = mutableListOf<Int>()
        val channel = Channel<Int>()
        val flow = channel.receiveAsFlow()
        // Act
        println("launch")
        launch {
            for (x in 1..5){
                println("send $x")
                channel.send(x * x)
                delay(50)
            }
            println("close")
            channel.close() // we're done sending
        }
        flow.collect{
            println("receive $it")
            received.add(it)
        }
        // Assert
        println("assert")
        assert(received == listOf(1, 4, 9, 16, 25))
    }
}