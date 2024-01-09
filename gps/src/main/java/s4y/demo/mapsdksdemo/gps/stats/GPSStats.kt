package s4y.demo.mapsdksdemo.gps.stats

import s4y.demo.mapsdksdemo.gps.GPSUpdate
import s4y.demo.mapsdksdemo.gps.data.Units
import kotlin.math.abs
import kotlin.math.sqrt

class GPSStats {
    class StatsData(
        val meanGps: Float,
        val sDeviationGps: Float,
        val meanEval: Float,
        val sDeviationEval: Float,
        val meanGpsEvalPrev: Float,
        val sDeviationGpsEvalPrev: Float,
        val meanEvalGpsCurrent: Float,
        val sDeviationEvalGpsCurrent: Float,
        val meanEvalGpsAvg: Float,
        val sDeviationEvalGpsAvg: Float,
        val maxAccelerationEval: Double,
        val maxAcceleration: Double,
        val minDt: Double,
        val maxDt: Double
    )

    private class Eval(val velocity: Float, val accel: Double)

    companion object {
        private fun meanDev(vals: List<Float>): Pair<Float, Float> {
            val mean = vals.average()
            val deviation = vals.map { it - mean }
            val squared = deviation.map { it * it }
            val variance = squared.average()
            val standardDeviation = Math.sqrt(variance)
            return Pair(mean.toFloat(), standardDeviation.toFloat())
        }
    }

    fun velocityStats(updates: Array<GPSUpdate>): StatsData {
        if (updates.size < 2) {
            return StatsData(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.0, 0.0, 0.0, 0.0)
        }
        var prevVelocity = -1.0
        val evals2 = Array(updates.size - 1) { i ->
            val lat1 = Units.Latitude.fromDegrees(updates[i].latitude)
            val lat2 = Units.Latitude.fromDegrees(updates[i + 1].latitude)
            val lon1 = Units.Longitude.fromDegrees(updates[i].longitude, lat1)
            val lon2 = Units.Longitude.fromDegrees(updates[i + 1].longitude, lat2)

            val latDistance = lat2.meters - lat1.meters
            val lonDistance = lon2.meters - lon1.meters

            val distance = sqrt(latDistance * latDistance + lonDistance * lonDistance)
            val time = updates[i + 1].ts - updates[i].ts
            val velocity = distance * 1000 / time

            val accel = if (prevVelocity == -1.0)
                0.0
            else
                abs(velocity - prevVelocity) * 1000 / time

            prevVelocity = velocity

            Eval(velocity.toFloat(), accel)
        }.toList()
        val evalVelocities = evals2.map { it.velocity }
        val gps = meanDev(updates.map { it.velocity })
        val eval = meanDev(evalVelocities)
        val gpsEvalPrev = meanDev(evalVelocities.mapIndexed { i, v -> updates[i].velocity - v })
        val gpsEvalCurrent = meanDev(evalVelocities.mapIndexed { i, v -> updates[i + 1].velocity - v })
        val gpsEvalAvg =
            meanDev(evalVelocities.mapIndexed { i, v -> (updates[i].velocity + updates[i + 1].velocity) / 2 - v })

        var maxAccelerationEval = 0.0
        for (i in 0 until evals2.size) {
            if (evals2[i].accel > maxAccelerationEval) {
                maxAccelerationEval = evals2[i].accel
            }
        }

        var minDt = 0.0
        var maxDt = 0.0

        var maxAcceleration = 0.0
        for (i in 1..<updates.size) {
            val time = (updates[i].ts - updates[i - 1].ts) / 1000.0
            if (time < minDt) {
                minDt = time
            }
            if (time > maxDt) {
                maxDt = time
            }
            if (time > 0) {

                val acceleration = abs((updates[i].velocity - updates[i - 1].velocity)) / time
                if (acceleration > maxAcceleration) {
                    maxAcceleration = acceleration
                }
            }
        }
        return StatsData(
            gps.first,
            gps.second,
            eval.first,
            eval.second,
            gpsEvalPrev.first,
            gpsEvalPrev.second,
            gpsEvalCurrent.first,
            gpsEvalCurrent.second,
            gpsEvalAvg.first,
            gpsEvalAvg.second,
            maxAccelerationEval,
            maxAcceleration,
            minDt,
            maxDt
        )
    }
}