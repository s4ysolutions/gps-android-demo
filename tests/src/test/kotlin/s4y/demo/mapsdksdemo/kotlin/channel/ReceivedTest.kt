package s4y.demo.mapsdksdemo.kotlin.channel

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class ReceivedTest {
    @Test
    fun receive_shouldSkipSentBefore() = runBlocking {
        // Arrange
        val received = mutableListOf<Int>()
        val channel = Channel<Int>()
        // Act
        println("=======> launch send")
        launch {
            println("=======> launched")
            for (x in 1..5) {
                println("=======> send ${x * x}")
                channel.trySend(x * x)
                delay(50)
            }
            println("=======> close")
            channel.close() // we're done sending
        }
        println("=======> delay before receive")
        delay(125)
        println("=======> delay end")
        for (y in channel) {
            println("=======> receive $y")
            received.add(y)
        }
        // Assert
        assert(received == listOf(16, 25))
    }
}