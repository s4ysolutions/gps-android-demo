package s4y.demo.mapsdksdemo.kotlin.delegate

import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty

class ThisRefTest {
    class FooClass {
        companion object {
            val instance = FooClass()
        }

        var x = 41
        operator fun getValue(nothing: Nothing?, property: KProperty<*>): Int {
            return x
        }

        operator fun setValue(nothing: Nothing?, property: KProperty<*>, i: Int) {
            x = i
        }
    }

    class BarClass {
        companion object {
            val instance = BarClass()
        }

        operator fun getValue(nothing: Nothing?, property: KProperty<*>): String {
            return "42"
        }
    }

    companion object {
        inline fun<reified T> fooOrBar(): T {
            return when(T::class) {
                FooClass::class -> FooClass.instance as T
                BarClass::class -> BarClass.instance as T
                else -> throw RuntimeException("Unknown class")
            }
        }
    }

    @Test
    fun getValue_shouldBeCalled() {
        val foo: Int by FooClass()
        val boo: String by BarClass()
        assert(foo == 41)
        assert(boo == "42")
    }
    @Test
    fun setValue_shouldBeCalled() {
        var foo: Int by FooClass()
        assert(foo == 41)
        foo = 40
        assert(foo == 40)
    }

    @Test
    fun provide_shouldProvideFoo() {
        val foo by fooOrBar<FooClass>()
        assert(foo == 41)
    }
    @Test
    fun provide_shouldProvideBar() {
        val bar by fooOrBar<BarClass>()
        assert(bar == "42")
    }
}