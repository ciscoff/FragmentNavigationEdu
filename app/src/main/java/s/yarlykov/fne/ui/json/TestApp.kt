package s.yarlykov.fne.ui.json

import com.google.gson.GsonBuilder

class Item(val code: String, val view: List<Item>? = null) {
    override fun toString() : String = "$code\n"
}


fun String.isOpen() = this.endsWith("[")
fun String.isClose() = this.endsWith("]")
fun String.isSpecial() = this.contains("[") or this.contains("]")
//fun String.

fun main(args: Array<String>) {
    val rawJson = listOf("crex", "fex", "pex[", "pups", "koko[", "baba]]", "max")

    val gson = GsonBuilder().create()

    val li = TestParser(rawJson).parse()

    System.out.println(gson.toJson(li))
}

class TestParser(private val rawJson : List<String>) {

    private val items = mutableListOf<Item>()

    private var brackets: Int = 0

    fun parse() : List<Item> = worker(0, items)


    private fun worker(i: Int, li: MutableList<Item>): List<Item> {

        if(i >= rawJson.size) return li

        val item = rawJson[i]

        when {
            item.isOpen() -> {
                brackets++
                val l = mutableListOf<Item>()
                li.add(Item(item.substringBefore("["), worker(i + 1, l)))
                return if(brackets > 0) li else worker(i + 1, li)


//                return worker(i + 1, li)
            }
            item.isClose() -> {
                --brackets
//                brackets -= Regex("]").findAll(item).count()
//                if(brackets != 0) throw Throwable("${this.javaClass.simpleName}:: Illegal input format")
                li.add(Item(code = item.substringBefore("]")))
                return li
            }
            else -> {
                li.add(Item(code = item))
                return worker(i + 1, li)
            }
        }

    }
}