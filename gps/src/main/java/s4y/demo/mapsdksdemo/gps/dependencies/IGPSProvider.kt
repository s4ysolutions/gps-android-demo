package s4y.demo.mapsdksdemo.gps.dependencies

interface IGPSProvider {
    enum class Priority {
        HIGH_ACCURACY,
        BALANCED_POWER_ACCURACY,
        LOW_POWER,
        PASSIVE, ;
    }

    enum class Granularity {
        PERMISSION_LEVEL,
        FINE,
        COARSE, ;
    }

    var durationMillis: Long
    var granularity: Granularity
    var priority: Priority
    var maxUpdateAgeMillis: Long
}
