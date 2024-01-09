package s4y.demo.mapsdksdemo.gps.tests

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FlowTest {
    @Test
    fun exception_shouldBeCaught() = runBlocking{
        val ex = Exception("test")
        val flow = flow<Int> {
            throw ex
        }

        var expected: Throwable? = null
        var result = 0
        flow.catch {
            expected = it
        }.collect{
            result = it
        }
        assert(expected == ex)
        assert(result == 0)
    }
}