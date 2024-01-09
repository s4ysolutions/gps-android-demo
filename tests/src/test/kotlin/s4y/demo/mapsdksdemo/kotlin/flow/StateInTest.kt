package s4y.demo.mapsdksdemo.kotlin.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StateInTest {
    @Test
    fun stateIn_shouldNotWaitFirstEmittedValue() = runBlocking {
        val flow = flow {
            delay(1000)
            emit(true)
        }
        val state = flow.stateIn(this, SharingStarted.Eagerly, false)
        assertEquals(false, state.value)
    }
}