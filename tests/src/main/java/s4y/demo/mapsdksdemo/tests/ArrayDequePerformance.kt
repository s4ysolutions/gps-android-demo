package s4y.demo.mapsdksdemo.tests

import java.util.UUID.randomUUID

class ArrayDequePerformance {


    fun measure(measured: () -> Unit) {
        val measurements = arrayOfNulls<Int>(100)
        for (i in measurements.indices) {
            val start = System.currentTimeMillis()

            measured()
            val end = System.currentTimeMillis()
            val time = (end - start).toInt()
            measurements[i] = time
        }

        val mean = measurements.fold(0) { acc, i -> acc + i!! } / measurements.size
        val stdDev = Math.sqrt(measurements.fold(0.0) { acc, i ->
            acc + Math.pow(
                (i!! - mean).toDouble(),
                2.0
            )
        }) / measurements.size
        println("mean: $mean, stdDev: $stdDev")
    }


    companion object {
        val length = 50000
        val arrayDeque = ArrayDeque<String>(length).also {
            for (i in 0..<length) {
                it.add(randomUUID().toString())
            }
        }

        val list = listOf<String>().also {
            for (i in 0..<length) {
                it.plus(randomUUID().toString())
            }
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val arrayDequePerformance = ArrayDequePerformance()
            arrayDequePerformance.measure {
                @Suppress("UNCHECKED_CAST")
                arrayDeque.toArray(arrayOfNulls<String>(arrayDeque.size)) as Array<String>
            }
            arrayDequePerformance.measure {
                arrayDeque.toList()
            }
        }
    }
}