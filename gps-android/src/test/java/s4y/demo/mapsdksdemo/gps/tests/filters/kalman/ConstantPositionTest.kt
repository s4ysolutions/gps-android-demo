package s4y.demo.mapsdksdemo.gps.tests.filters.kalman

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Test
import s4y.demo.mapsdksdemo.gps.filters.kalman.data.StateVector
import s4y.demo.mapsdksdemo.gps.filters.kalman.GPSFilterKalmanConstantPosition
import kotlin.math.min

class ConstantPositionTest {
    @Test
    fun reset_shouldNotCrash() {
        // Arrange
        val filter = GPSFilterKalmanConstantPosition.instance
        val ts = System.currentTimeMillis()
        // Act
        filter.reset(gpsPosition0, defaultDt)
    }

    @Test
    fun reset_shouldProvideFirstEstimation() {
        // Arrange
        val filter = GPSFilterKalmanConstantPosition.instance
        val ts = System.currentTimeMillis()
        filter.reset(gpsPosition0, defaultDt)
        // Act
        val estimation = StateVector.LongitudeLatitude(filter.kalmanFilter.stateEstimation)
        // Assert
        assertThat(estimation.latitude.degrees).isWithin(1e-6).of(latitude0)
        assertThat(estimation.longitude.degrees).isWithin(1e-6).of(longitude0)
    }

    @Test
    fun apply_shouldNotCrash() {
        val filter = GPSFilterKalmanConstantPosition.instance
        val ts = System.currentTimeMillis()
        filter.reset(gpsPosition0, defaultDt)
        val gpsPosition1 = s4y.demo.mapsdksdemo.gps.GPSUpdate(
            latitude0 + 1e04,
            longitude0 + 1e04,
            velocity0,
            gpsAccuracy0,
            bearing0d,
            ts + defaultDt
        )
        // Act
        filter.apply(gpsPosition1)
    }

    @Test
    fun track_shouldNotCrach() {
        val filter = GPSFilterKalmanConstantPosition.instance
        track.forEach { data ->
            val gpsUpdate = trackToGPSUpdate(data)
            filter.apply(gpsUpdate)
        }
    }

    @Test
    fun track_shouldPassAllUpdates() {
        // Arrange
        val filter = GPSFilterKalmanConstantPosition.instance
        val filtered = mutableListOf<s4y.demo.mapsdksdemo.gps.GPSUpdate>()
        // Act
        for(i in 0 until track.size ) {
            val gpsUpdate = trackToGPSUpdate(track[i])
            filtered.add(filter.apply(gpsUpdate))
        }
        // Assert
        // val scv = GPSUpdatesSerializerCSV().serialize(filtered.toTypedArray())
        // println(scv)
        assertThat(filtered.size).isEqualTo(track.size)
    }

    @Test
    fun filtered_shouldBeSameDepsiteOfNumberOfMeasurements() {
        // Arrange
        val filter = GPSFilterKalmanConstantPosition.instance
        // Act
        val lst = mutableListOf<List<s4y.demo.mapsdksdemo.gps.GPSUpdate>>()
        for (i in 3 until track.size) {
            val filtered = mutableListOf<s4y.demo.mapsdksdemo.gps.GPSUpdate>()
            filter.reset() // trackToGPSUpdate(track[0]), defaultDt)
            for (j in 0 until i) {
                val gpsUpdate = trackToGPSUpdate(track[j])
                filtered.add(filter.apply(gpsUpdate))
            }
            lst.add(filtered)
        }
        // Assert
        lst.forEachIndexed { n, filteredN ->
            lst.forEachIndexed { m, filteredM ->
                for (i in 0 until min(filteredN.size, filteredM.size)) {
                    assertWithMessage("latitude $n/$m/$i").that(filteredN[i].latitude).isWithin(1e-6).of(filteredM[i].latitude)
                    assertWithMessage("longitude $n/$m/$i").that(filteredN[i].longitude).isWithin(1e-6).of(filteredM[i].longitude)
                }
            }
        }
    }

    companion object {
        const val latitude0 = 45.0
        const val longitude0 = 20.0
        const val velocity0 = 1.0f // 1m/s
        const val gpsAccuracy0 = 5.0f
        const val bearing0d = 15.0f // degrees
        val ts0 = System.currentTimeMillis()
        val gpsPosition0 = s4y.demo.mapsdksdemo.gps.GPSUpdate(
            latitude0,
            longitude0,
            velocity0,
            gpsAccuracy0,
            bearing0d,
            ts0
        )
        val defaultDt = 1000L // 1s

        fun trackToGPSUpdate(data: DoubleArray): s4y.demo.mapsdksdemo.gps.GPSUpdate {
            return s4y.demo.mapsdksdemo.gps.GPSUpdate(
                data[0],
                data[1],
                data[2].toFloat(),
                data[3].toFloat(),
                data[4].toFloat(),
                data[5].toLong()
            )
        }

        // ts,latitude,longitude,velocity,bearing,accuracy
        val track = arrayOf(
            doubleArrayOf(45.2450892, 19.8249205, 0.0, 13.77, 0.0, 1705781269541.0),
            doubleArrayOf(45.2450933, 19.8249507, 0.3382222, 13.851, 79.07112, 1705781274492.0),
            doubleArrayOf(45.245094, 19.8249647, 0.8090474, 14.362, 83.67982, 1705781274844.0),
            doubleArrayOf(45.2450947, 19.8249677, 0.34675246, 14.517, 58.873188, 1705781275546.0),
            doubleArrayOf(45.2450961, 19.8249729, 0.01704794, 14.546, 0.0, 1705781276540.0),
            doubleArrayOf(45.2450963, 19.8249771, 0.0015523637, 14.512, 0.0, 1705781277544.0),
            doubleArrayOf(45.245096, 19.8249812, 0.3569261, 14.464, 56.805855, 1705781278545.0),
            doubleArrayOf(45.2450962, 19.8249872, 0.47846803, 14.491, 20.18289, 1705781279569.0),
            doubleArrayOf(45.2451003, 19.8249909, 0.79178816, 14.238, 315.99567, 1705781280555.0),
            doubleArrayOf(45.2451023, 19.8249884, 0.8643789, 14.057, 285.04648, 1705781281550.0),
            doubleArrayOf(45.2451013, 19.8249839, 0.7016683, 14.001, 275.10504, 1705781282539.0),
            doubleArrayOf(45.2450855, 19.8250465, 0.44702682, 13.781, 268.2655, 1705781283547.0),
            doubleArrayOf(45.2450708, 19.8250996, 0.9579311, 13.651, 92.25399, 1705781284558.0),
            doubleArrayOf(45.2450582, 19.8251393, 1.6861368, 13.389, 95.07893, 1705781285560.0),
            doubleArrayOf(45.2450473, 19.8251702, 1.8141328, 13.044, 103.77898, 1705781286565.0),
            doubleArrayOf(45.2450368, 19.8251887, 1.6022052, 12.7, 107.86133, 1705781287554.0),
            doubleArrayOf(45.2450312, 19.8251953, 1.0675225, 12.666, 100.62258, 1705781288548.0),
            doubleArrayOf(45.2450297, 19.8251914, 0.46521586, 12.416, 104.88439, 1705781289542.0),
            doubleArrayOf(45.2450276, 19.8251786, 0.118982755, 11.8, 189.67513, 1705781290567.0),
            doubleArrayOf(45.2450238, 19.8251629, 0.24662259, 11.5, 213.22536, 1705781291564.0),
            doubleArrayOf(45.2450227, 19.8251555, 0.21485366, 11.166, 228.797, 1705781292546.0),
            doubleArrayOf(45.2450208, 19.8251449, 0.15921144, 10.4, 180.0, 1705781293560.0),
            doubleArrayOf(45.2450202, 19.8251418, 0.107178494, 10.1, 180.0, 1705781294562.0),
            doubleArrayOf(45.2450202, 19.8251398, 0.05348829, 10.0, 0.0, 1705781295553.0),
            doubleArrayOf(45.2450207, 19.8251356, 0.021427805, 9.833, 0.0, 1705781296548.0),
            doubleArrayOf(45.2450205, 19.8251231, 0.29434592, 9.7, 274.15823, 1705781297559.0),
            doubleArrayOf(45.2450197, 19.8251035, 0.7399809, 9.6, 271.4078, 1705781298563.0),
            doubleArrayOf(45.2450183, 19.8250824, 0.99645156, 9.5, 269.68484, 1705781299557.0),
            doubleArrayOf(45.2450203, 19.8250707, 0.21718353, 9.25, 307.72638, 1705781300554.0),
            doubleArrayOf(45.245022, 19.825059, 0.1790623, 9.1, 355.02924, 1705781301549.0),
            doubleArrayOf(45.2450231, 19.8250491, 0.15437701, 9.083, 1.4297222, 1705781302556.0),
            doubleArrayOf(45.2450232, 19.8250421, 0.10263898, 8.916, 352.2488, 1705781303563.0),
            doubleArrayOf(45.245026, 19.8250391, 0.19643115, 8.8, 359.99988, 1705781304563.0),
            doubleArrayOf(45.2450301, 19.8250358, 0.32615504, 8.7, 358.0231, 1705781305562.0),
            doubleArrayOf(45.2450319, 19.8250327, 0.26086336, 8.5, 356.0763, 1705781306562.0),
            doubleArrayOf(45.2450308, 19.825033, 0.10342854, 8.3, 337.94882, 1705781307563.0),
            doubleArrayOf(45.2450305, 19.825032, 0.030310582, 8.25, 0.0, 1705781308556.0),
            doubleArrayOf(45.2450303, 19.8250316, 0.0010405356, 8.25, 0.0, 1705781309554.0),
            doubleArrayOf(45.2450301, 19.8250315, 0.006359782, 8.1, 0.0, 1705781310567.0),
            doubleArrayOf(45.2450301, 19.8250314, 0.0053362376, 8.083, 0.0, 1705781311549.0),
            doubleArrayOf(45.2450301, 19.8250313, 0.0029684633, 8.083, 0.0, 1705781312544.0),
            doubleArrayOf(45.2450301, 19.8250313, 0.00125123, 8.1, 0.0, 1705781313557.0),
            doubleArrayOf(45.2450301, 19.8250313, 3.8443005E-4, 8.083, 0.0, 1705781314553.0),
            doubleArrayOf(45.2450301, 19.8250313, 4.3587763E-5, 8.0, 0.0, 1705781315560.0),
            doubleArrayOf(45.2450301, 19.8250313, 4.8620634E-5, 7.9, 0.0, 1705781316563.0),
            doubleArrayOf(45.2450301, 19.8250313, 4.625969E-5, 7.8, 0.0, 1705781317553.0),
            doubleArrayOf(45.2450301, 19.8250313, 2.6259046E-5, 7.7, 0.0, 1705781318563.0),
            doubleArrayOf(45.2450301, 19.8250313, 1.1298751E-5, 7.6, 0.0, 1705781319562.0),
            doubleArrayOf(45.2450301, 19.8250313, 3.5679068E-6, 7.5, 0.0, 1705781320565.0),
            doubleArrayOf(45.2450301, 19.8250313, 5.384604E-7, 7.5, 0.0, 1705781321557.0),
            doubleArrayOf(45.2450301, 19.8250313, 2.9644696E-7, 7.5, 0.0, 1705781322559.0),
            doubleArrayOf(45.2450301, 19.8250313, 3.3887721E-7, 7.5, 0.0, 1705781323563.0),
            doubleArrayOf(45.2450301, 19.8250313, 2.039913E-7, 7.416, 0.0, 1705781324551.0),
            doubleArrayOf(45.2450301, 19.8250313, 9.330023E-8, 7.416, 0.0, 1705781325555.0),
            doubleArrayOf(45.2450301, 19.8250313, 3.006346E-8, 7.333, 0.0, 1705781326548.0),
            doubleArrayOf(45.2450301, 19.8250313, 5.546593E-9, 7.25, 0.0, 1705781327547.0),
            doubleArrayOf(45.2450301, 19.8250313, 1.4730464E-9, 7.166, 0.0, 1705781328562.0),
            doubleArrayOf(45.2450301, 19.8250313, 2.1349933E-9, 7.1, 0.0, 1705781329557.0),
            doubleArrayOf(45.2450301, 19.8250313, 1.3336071E-9, 7.0, 0.0, 1705781330557.0),
            doubleArrayOf(45.2450301, 19.8250313, 6.043125E-10, 7.0, 0.0, 1705781331560.0),
            doubleArrayOf(45.2450301, 19.8250313, 2.0511573E-10, 7.0, 0.0, 1705781332564.0),
            doubleArrayOf(45.2450301, 19.8250313, 4.2183573E-11, 7.0, 0.0, 1705781333558.0),
            doubleArrayOf(45.2450301, 19.8250313, 6.7301416E-12, 7.0, 0.0, 1705781334559.0),
            doubleArrayOf(45.2450267, 19.8250284, 0.034015466, 7.0, 0.0, 1705781335554.0),
            doubleArrayOf(45.245023, 19.8250267, 0.119189195, 7.0, 251.89586, 1705781336563.0),
            doubleArrayOf(45.245021, 19.8250257, 0.1499906, 7.0, 222.08403, 1705781337548.0),
            doubleArrayOf(45.2450225, 19.8250269, 0.020038929, 7.0, 0.0, 1705781338562.0),
            doubleArrayOf(45.2450238, 19.8250283, 0.052033585, 7.0, 0.0, 1705781339557.0),
            doubleArrayOf(45.2450244, 19.8250282, 0.0485709, 7.0, 0.0, 1705781340555.0),
            doubleArrayOf(45.2450245, 19.8250282, 0.027898852, 7.0, 0.0, 1705781341565.0),
            doubleArrayOf(45.2450245, 19.8250282, 0.011959919, 7.0, 0.0, 1705781342553.0),
            doubleArrayOf(45.2450286, 19.8250251, 0.19919379, 7.0, 358.8699, 1705781343564.0),
            doubleArrayOf(45.2450316, 19.8250199, 0.26384526, 7.0, 321.68884, 1705781344558.0),
            doubleArrayOf(45.2450316, 19.8250134, 0.31957263, 6.9, 281.50348, 1705781345561.0),
            doubleArrayOf(45.2450314, 19.8250123, 0.016833559, 6.833, 0.0, 1705781346558.0),
            doubleArrayOf(45.2450313, 19.8250123, 4.2510126E-4, 6.75, 0.0, 1705781347551.0),
            doubleArrayOf(45.2450312, 19.8250123, 0.002678008, 6.666, 0.0, 1705781348559.0),
            doubleArrayOf(45.2450312, 19.8250123, 0.0020092488, 6.5, 0.0, 1705781349560.0),
            doubleArrayOf(45.2450312, 19.8250123, 0.0010082158, 6.6, 0.0, 1705781350567.0),
            doubleArrayOf(45.2450312, 19.8250123, 3.8235323E-4, 6.666, 0.0, 1705781351556.0),
            doubleArrayOf(45.2450312, 19.8250123, 9.9989564E-5, 6.75, 0.0, 1705781352547.0),
            doubleArrayOf(45.2450312, 19.8250123, 6.7878517E-7, 6.75, 0.0, 1705781353552.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.572599E-5, 6.9, 0.0, 1705781354562.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.1996758E-5, 6.833, 0.0, 1705781355563.0),
            doubleArrayOf(45.2450312, 19.8250123, 5.9764584E-6, 6.7, 0.0, 1705781356562.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.2529039E-6, 6.6, 0.0, 1705781357556.0),
            doubleArrayOf(45.2450312, 19.8250123, 6.3172575E-7, 6.7, 0.0, 1705781358562.0),
            doubleArrayOf(45.2450312, 19.8250123, 4.5955115E-8, 6.75, 0.0, 1705781359561.0),
            doubleArrayOf(45.2450312, 19.8250123, 8.7341824E-8, 6.666, 0.0, 1705781360557.0),
            doubleArrayOf(45.2450312, 19.8250123, 7.0266424E-8, 6.7, 0.0, 1705781361565.0),
            doubleArrayOf(45.2450312, 19.8250123, 3.651554E-8, 6.8, 0.0, 1705781362563.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.3684628E-8, 6.75, 0.0, 1705781363558.0),
            doubleArrayOf(45.2450312, 19.8250123, 3.6922592E-9, 6.666, 0.0, 1705781364558.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.9381217E-10, 6.6, 0.0, 1705781365559.0),
            doubleArrayOf(45.2450312, 19.8250123, 4.0567288E-10, 6.7, 0.0, 1705781366591.0),
            doubleArrayOf(45.2450312, 19.8250123, 3.6586145E-10, 6.7, 0.0, 1705781367563.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.003509E-10, 6.8, 0.0, 1705781368560.0),
            doubleArrayOf(45.2450312, 19.8250123, 8.2252566E-11, 6.9, 0.0, 1705781369564.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.3117824E-11, 6.9, 0.0, 1705781370562.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.760066E-12, 6.833, 0.0, 1705781371563.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.0084318E-12, 6.75, 0.0, 1705781372558.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.930185E-12, 6.6, 0.0, 1705781373563.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.0728684E-12, 6.666, 0.0, 1705781374556.0),
            doubleArrayOf(45.2450312, 19.8250123, 4.4041615E-13, 6.666, 0.0, 1705781375560.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.3225337E-13, 6.75, 0.0, 1705781376557.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.7170388E-14, 6.833, 0.0, 1705781377554.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.1759425E-14, 6.916, 0.0, 1705781378558.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.2062646E-14, 7.0, 0.0, 1705781379550.0),
            doubleArrayOf(45.2450312, 19.8250123, 6.7110924E-15, 7.0, 0.0, 1705781380548.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.493537E-15, 7.0, 0.0, 1705781381557.0),
            doubleArrayOf(45.2450312, 19.8250123, 6.2116764E-16, 7.0, 0.0, 1705781382556.0),
            doubleArrayOf(45.2450312, 19.8250123, 9.5079203E-17, 7.0, 0.0, 1705781383553.0),
            doubleArrayOf(45.2450312, 19.8250123, 8.669523E-17, 7.1, 0.0, 1705781384559.0),
            doubleArrayOf(45.2450312, 19.8250123, 7.904222E-17, 7.2, 0.0, 1705781385564.0),
            doubleArrayOf(45.2450312, 19.8250123, 7.206493E-17, 7.3, 0.0, 1705781386560.0),
            doubleArrayOf(45.2450312, 19.8250123, 6.577442E-17, 7.3, 0.0, 1705781387560.0),
            doubleArrayOf(45.2450312, 19.8250123, 5.9969105E-17, 7.4, 0.0, 1705781388557.0),
            doubleArrayOf(45.2450312, 19.8250123, 5.467697E-17, 7.4, 0.0, 1705781389560.0),
            doubleArrayOf(45.2450312, 19.8250123, 4.9852283E-17, 7.416, 0.0, 1705781390555.0),
            doubleArrayOf(45.2450312, 19.8250123, 4.549788E-17, 7.333, 0.0, 1705781391560.0),
            doubleArrayOf(45.2450312, 19.8250123, 4.153182E-17, 7.25, 0.0, 1705781392554.0),
            doubleArrayOf(45.2450312, 19.8250123, 3.7911097E-17, 7.2, 0.0, 1705781393560.0),
            doubleArrayOf(45.2450312, 19.8250123, 3.4605994E-17, 7.1, 0.0, 1705781394565.0),
            doubleArrayOf(45.2450312, 19.8250123, 3.1606766E-17, 7.083, 0.0, 1705781395542.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.8851468E-17, 7.0, 0.0, 1705781396559.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.6336577E-17, 7.0, 0.0, 1705781397562.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.4127072E-17, 6.75, 0.0, 1705781398558.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.2085793E-17, 6.583, 0.0, 1705781399560.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.0225102E-17, 6.3, 0.0, 1705781400559.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.8540344E-17, 6.0, 0.0, 1705781401565.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.7002683E-17, 5.916, 0.0, 1705781402555.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.55723E-17, 5.8, 0.0, 1705781403562.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.4279879E-17, 5.75, 0.0, 1705781404558.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.3067397E-17, 5.833, 0.0, 1705781405542.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.1970489E-17, 5.833, 0.0, 1705781406535.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.0948463E-17, 6.0, 0.0, 1705781407534.0),
            doubleArrayOf(45.2450312, 19.8250123, 1.0021691E-17, 6.083, 0.0, 1705781408547.0),
            doubleArrayOf(45.2450312, 19.8250123, 9.176634E-18, 6.083, 0.0, 1705781409535.0),
            doubleArrayOf(45.2450312, 19.8250123, 8.412359E-18, 6.083, 0.0, 1705781410538.0),
            doubleArrayOf(45.2450312, 19.8250123, 7.700105E-18, 6.0, 0.0, 1705781411561.0),
            doubleArrayOf(45.2450312, 19.8250123, 7.051809E-18, 5.9, 0.0, 1705781412555.0),
            doubleArrayOf(45.2450312, 19.8250123, 6.457113E-18, 5.9, 0.0, 1705781413560.0),
            doubleArrayOf(45.2450312, 19.8250123, 5.9124802E-18, 5.9, 0.0, 1705781414561.0),
            doubleArrayOf(45.2450312, 19.8250123, 5.4146213E-18, 6.0, 0.0, 1705781415555.0),
            doubleArrayOf(45.2450312, 19.8250123, 4.9636755E-18, 5.916, 0.0, 1705781416560.0),
            doubleArrayOf(45.2450312, 19.8250123, 4.551255E-18, 5.8, 0.0, 1705781417558.0),
            doubleArrayOf(45.2450312, 19.8250123, 4.17838E-18, 5.666, 0.0, 1705781418559.0),
            doubleArrayOf(45.2450312, 19.8250123, 3.832764E-18, 5.583, 0.0, 1705781419555.0),
            doubleArrayOf(45.2450312, 19.8250123, 3.51691E-18, 5.5, 0.0, 1705781420539.0),
            doubleArrayOf(45.2450312, 19.8250123, 3.2257974E-18, 5.416, 0.0, 1705781421541.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.9572925E-18, 5.416, 0.0, 1705781422557.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.7124088E-18, 5.416, 0.0, 1705781423546.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.4869088E-18, 5.5, 0.0, 1705781424559.0),
            doubleArrayOf(45.2450312, 19.8250123, 2.2806277E-18, 5.5, 0.0, 1705781425557.0),
        )
    }
}
