package s4y.demo.mapsdksdemo.appstate.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty

abstract class BasePreference<T : Any>(private val context: Context) {
    abstract val key: String
    abstract val default: T
    fun getPreferences(): SharedPreferences =
        context.getSharedPreferences("appstate", Context.MODE_PRIVATE)

    fun <TT : T> set(value: TT) {
        val edit = getPreferences().edit()
        when (value) {
            is Int -> edit.putInt(key, value).apply()
            is Float -> edit.putFloat(key, value).apply()
            is Double -> edit.putFloat(key, value.toFloat()).apply()
            is String -> edit.putString(key, value).apply()
            is Boolean -> edit.putBoolean(key, value).apply()
            else -> throw IllegalArgumentException("Unsupported type $value")
        }
    }

    inline fun <reified TT : T> get(): TT {
        val preferences = getPreferences()
        val clazz = TT::class.java
        return when (clazz) {
            java.lang.Integer::class.java,
            Int::class.java -> preferences.getInt(key, default as Int)

            java.lang.Float::class.java,
            Float::class.java -> preferences.getFloat(key, default as Float)

            java.lang.Double::class.java,
            Double::class.java -> preferences.getFloat(key, (default as Double).toFloat()).toDouble()

            java.lang.String::class.java,
            String::class.java -> preferences.getString(key, default as String)

            java.lang.Boolean::class.java,
            Boolean::class.java -> preferences.getBoolean(key, default as Boolean)
            else -> throw IllegalArgumentException("Unsupported type ${TT::class.java}")
        } as TT
    }

    @Suppress("UNUSED_PARAMETER")
    inline fun <reified TT : T> get(nothing: Nothing?, property: KProperty<*>): T =
        get<TT>()

    @Suppress("UNUSED_PARAMETER")
    fun <TT : T> setValue(nothing: Nothing?, property: KProperty<*>, value: TT) =
        set(value)
}