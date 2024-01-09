package s4y.demo.mapsdksdemo.appstate.preferences

import android.content.Context
import android.util.Base64
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import kotlin.reflect.KProperty

abstract class SerializedPreference<T : Serializable>(context: Context) {
    abstract val key: String
    abstract val default: T

    private val preference = object : BasePreference<String>(context) {
        override val key: String get() = this@SerializedPreference.key
        override val default: String = ""
    }

    fun get(): T {
        val serialized = preference.get<String>()
        if (serialized.isEmpty()) {
            return default
        }
        val byteArray = Base64.decode(serialized, Base64.DEFAULT)
        val byteArrayInputStream = ByteArrayInputStream(byteArray)
        val objectInputStream = ObjectInputStream(byteArrayInputStream)
        try {
            @Suppress("UNCHECKED_CAST")
            return objectInputStream.readObject() as T
        } catch (e: Exception) {
            // TODO: log
            e.printStackTrace()
            return default
        }
    }

    open fun set(v: T) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(v)
        objectOutputStream.close()
        val serialized = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        preference.set(serialized)
        _stateFlow.value = v
    }

    private val _stateFlow = MutableStateFlow(get())
    fun asStateFlow():StateFlow<T> = _stateFlow

    // by accessors
    operator fun getValue(companion: Any, property: KProperty<*>): T = get()
    operator fun setValue(companion: Any, property: KProperty<*>, value: T) = set(value)
}