package s4y.demo.mapsdksdemo.gps.implementation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import s4y.demo.mapsdksdemo.gps.GPSUpdate
import s4y.demo.mapsdksdemo.gps.dependencies.IGPSUpdatesStore
import s4y.demo.mapsdksdemo.gps.filters.GPSFilter
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ArrayGPSUpdatesStore(capacity: Int) : IGPSUpdatesStore {
    private var buffer = Array(capacity) { GPSUpdate.emptyGPSUpdate }
    private var head = 0
    private var tail = 0
    private val lock = ReentrantLock()
    override var capacity: Int
        get() = lock.withLock { buffer.size }
        set(value) = lock.withLock {
            buffer = Array(value) { GPSUpdate.emptyGPSUpdate }
            clear()
        }

    override val updates: MutableStateFlow<Array<GPSUpdate>> =
        MutableStateFlow(emptyArray<GPSUpdate>())

    override val lastUpdate: MutableSharedFlow<GPSUpdate> =
        MutableSharedFlow(1, 0, BufferOverflow.DROP_OLDEST)

    private fun <T> withNotify(block: () -> T): T {
        try {
            return block()
        } finally {
            lock.withLock {
                if (head == tail) {
                    updates.value = emptyArray()
                } else if (head < tail) {
                    updates.value = buffer.copyOfRange(head, tail)
                } else {
                    updates.value =
                        buffer.copyOfRange(head, buffer.size) + buffer.copyOfRange(0, tail)
                }
            }
        }
    }

    override val size: Int
        get() = lock.withLock {
            if (tail >= head) tail - head else tail + buffer.size - head
        }

    override fun add(element: GPSUpdate): Boolean = withNotify {
        lock.withLock {
            buffer[tail] = element
            tail = (tail + 1) % buffer.size
            if (tail == head) {
                head = (head + 1) % buffer.size
            }
            true
        }
        lastUpdate.tryEmit(element)
    }

    override fun addAll(elements: Collection<GPSUpdate>): Boolean = withNotify {
        lock.withLock {
            for (element in elements) {
                add(element)
            }
            true
        }
    }

    override fun clear() = withNotify {
        lock.withLock {
            head = 0
            tail = 0
        }
    }

    override fun contains(element: GPSUpdate): Boolean = lock.withLock {
        if (head < tail) {
            for (i in head until tail) {
                if (buffer[i] == element) {
                    return true
                }
            }
        } else {
            for (i in head until buffer.size) {
                if (buffer[i] == element) {
                    return true
                }
            }
            for (i in 0 until tail) {
                if (buffer[i] == element) {
                    return true
                }
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<GPSUpdate>): Boolean = lock.withLock {
        for (element in elements) {
            if (!contains(element)) {
                return false
            }
        }
        return true
    }

    override fun isEmpty(): Boolean = lock.withLock {
        return head == tail
    }

    override fun iterator(): MutableIterator<GPSUpdate> {
        return object : MutableIterator<GPSUpdate> {
            private var index = head
            override fun hasNext(): Boolean = lock.withLock {
                return index != tail
            }

            override fun next(): GPSUpdate = lock.withLock {
                val result = buffer[index]
                if (result.isEmpty)
                    throw NoSuchElementException()
                index = (index + 1) % buffer.size
                return result
            }

            override fun remove() {
                throw UnsupportedOperationException()
            }
        }
    }

    override fun remove(element: GPSUpdate): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAll(elements: Collection<GPSUpdate>): Boolean = withNotify {
        lock.withLock {
            val result = tail == head
            tail = head
            result
        }
    }

    override fun retainAll(elements: Collection<GPSUpdate>): Boolean {
        TODO("Not yet implemented")
    }
}