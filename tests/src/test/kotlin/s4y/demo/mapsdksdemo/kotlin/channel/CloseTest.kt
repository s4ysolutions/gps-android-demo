package s4y.demo.mapsdksdemo.kotlin.channel

import kotlinx.coroutines.channels.Channel
import org.junit.jupiter.api.Test

class CloseTest {
    @Test
    fun channel_shouldBeClosedAfterClose() {
        // Arrange
        val channel = Channel<Int>()
        // Act
        channel.close()
        // Assert
        assert(channel.isClosedForSend)
        assert(channel.isClosedForReceive)
    }
    @Test
    fun channel_shouldNotBeClosedBeforeClose() {
        // Arrange
        val channel = Channel<Int>()
        // Act
        // Assert
        assert(!channel.isClosedForSend)
        assert(!channel.isClosedForReceive)
        channel.close()
    }
}