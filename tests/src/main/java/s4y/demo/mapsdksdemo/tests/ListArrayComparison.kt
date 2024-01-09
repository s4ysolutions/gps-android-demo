package s4y.demo.mapsdksdemo.tests

class ListArrayComparison {
    private val list = listOf(1, 2, 3)
    private val array = arrayOf(1, 2, 3)

    fun printList() {
        list.forEach { println(it) }
    }

    fun printArray() {
        array.forEach { println(it) }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val listArrayComparison = ListArrayComparison()
            listArrayComparison.printList()
            listArrayComparison.printArray()
        }
    }
}