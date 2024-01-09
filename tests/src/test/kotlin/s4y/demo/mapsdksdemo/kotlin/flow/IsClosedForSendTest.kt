package s4y.demo.mapsdksdemo.kotlin.flow

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class IsClosedForSendTest {
    class Cb {
        var callback: ((Int) -> Unit)? = null
        fun call(i: Int) {
            callback?.invoke(i)
        }
    }

    @Test
    fun flow_shouldBeMarkedAsClosedForSend() = runBlocking{
        // Arrange
        val cb = Cb()
        val flow = callbackFlow {
            println("=======> subscribed")
            cb.callback = {
                println("=======> cb $it")
                trySend(it)
            }
            awaitClose {
                println("=======> awaitClose")
            }
        }
        val l = mutableListOf<Int>()
        // Act
        val job = launch {
            println("=======> start collect")

            flow.collect{
                println("=======> collect $it")
                l.add(it*it)
                println("=======> added")
            }
            println("=======> end collect")
        }
        println("=======> wait before call cb")
        delay(100)
        println("=======> call cb")
        cb.call(1)
        println("=======> wait after call cb")
        delay(100)
        // println("=======> flow.toList")
       // val l = flow.toList()
        println("=======> assert $l")
        assertContentEquals(listOf(1), l)
        job.cancel()
    }
}