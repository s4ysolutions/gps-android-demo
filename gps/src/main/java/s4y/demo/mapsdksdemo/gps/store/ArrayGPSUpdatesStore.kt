package s4y.demo.mapsdksdemo.gps.store

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import s4y.demo.mapsdksdemo.gps.store.dependencies.IGPSUpdatesStorage
import s4y.demo.mapsdksdemo.gps.store.dependencies.IGPSUpdatesStore
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ArrayGPSUpdatesStore(capacity: Int, private val persistentStorage: IGPSUpdatesStorage) :
    IGPSUpdatesStore {
    private val lockBuffer = ReentrantLock()
    private var buffer = Array(capacity) { s4y.demo.mapsdksdemo.gps.GPSUpdate.emptyGPSUpdate }
    private var head = 0
    private var tail = 0
    override var capacity: Int
        get() = lockBuffer.withLock { buffer.size }
        set(value) = lockBuffer.withLock {
            buffer = Array(value) { s4y.demo.mapsdksdemo.gps.GPSUpdate.emptyGPSUpdate }
            clear()
        }

    private val lockUpdates = ReentrantLock()
    private var _updates = emptyArray<s4y.demo.mapsdksdemo.gps.GPSUpdate>()
    override val snapshot: Array<s4y.demo.mapsdksdemo.gps.GPSUpdate>
        get() = lockUpdates.withLock {
            _updates
        }

    override val lastUpdate: MutableSharedFlow<s4y.demo.mapsdksdemo.gps.GPSUpdate> =
        MutableSharedFlow(1, 0, BufferOverflow.DROP_OLDEST)


    private val serializer = GPSUpdatesSerializerCSV()
    override fun saveAsFile(): String {
        return persistentStorage.save(snapshot, serializer)
    }

    private fun <T> withNotify(block: () -> T): T {
        try {
            return block()
        } finally {
            lockBuffer.withLock {
                if (head == tail) {
                    lockUpdates.withLock {
                        _updates = emptyArray()
                    }
                } else if (head < tail) {
                    lockUpdates.withLock {
                        _updates = buffer.copyOfRange(head, tail)
                    }
                } else {
                    lockUpdates.withLock {
                        _updates =
                            buffer.copyOfRange(head, buffer.size) + buffer.copyOfRange(0, tail)
                    }
                }
            }
        }
    }

    override val size: Int
        get() = lockBuffer.withLock {
            if (tail >= head) tail - head else tail + buffer.size - head
        }

    override fun add(element: s4y.demo.mapsdksdemo.gps.GPSUpdate): Boolean = withNotify {
        lockBuffer.withLock {
            buffer[tail] = element
            tail = (tail + 1) % buffer.size
            if (tail == head) {
                head = (head + 1) % buffer.size
            }
            true
        }
        lastUpdate.tryEmit(element)
    }

    override fun addAll(elements: Collection<s4y.demo.mapsdksdemo.gps.GPSUpdate>): Boolean = withNotify {
        lockBuffer.withLock {
            for (element in elements) {
                add(element)
            }
            true
        }
    }

    override fun clear() = withNotify {
        lockBuffer.withLock {
            head = 0
            tail = 0
        }
    }

    override fun contains(element: s4y.demo.mapsdksdemo.gps.GPSUpdate): Boolean = lockBuffer.withLock {
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

    override fun containsAll(elements: Collection<s4y.demo.mapsdksdemo.gps.GPSUpdate>): Boolean = lockBuffer.withLock {
        for (element in elements) {
            if (!contains(element)) {
                return false
            }
        }
        return true
    }

    override fun isEmpty(): Boolean = lockBuffer.withLock {
        return head == tail
    }

    override fun iterator(): MutableIterator<s4y.demo.mapsdksdemo.gps.GPSUpdate> {
        return object : MutableIterator<s4y.demo.mapsdksdemo.gps.GPSUpdate> {
            private var index = head
            override fun hasNext(): Boolean = lockBuffer.withLock {
                return index != tail
            }

            override fun next(): s4y.demo.mapsdksdemo.gps.GPSUpdate = lockBuffer.withLock {
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

    override fun remove(element: s4y.demo.mapsdksdemo.gps.GPSUpdate): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAll(elements: Collection<s4y.demo.mapsdksdemo.gps.GPSUpdate>): Boolean = withNotify {
        lockBuffer.withLock {
            val result = tail == head
            tail = head
            result
        }
    }

    override fun retainAll(elements: Collection<s4y.demo.mapsdksdemo.gps.GPSUpdate>): Boolean {
        TODO("Not yet implemented")
    }
}