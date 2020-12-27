package s.yarlykov.fne.generics_edu


open class Item<T : Any> {

    private lateinit var d : T

    fun put(data : T) {

    }
    fun fetch() : T {
        return d
    }
}

class ItemChild<T : Any> : Item<T>() {

}

fun useItem(item : Item<*>) {
    println("${item::class.java.simpleName}")
}

fun main(args : Array<String>) {

    useItem(ItemChild<String>())

}


