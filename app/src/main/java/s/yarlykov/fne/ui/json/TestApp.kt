package s.yarlykov.fne.ui.json

import com.google.gson.GsonBuilder

class Item(val code: String, val view: List<Item>? = null) {
    override fun toString() : String = "$code\n"
}

fun String.isOpen() = this.endsWith("[")
fun String.isClose() = this.endsWith("]")

fun main(args: Array<String>) {
    val rawJson = listOf("crex", "fex", "pex[", "pups", "koko[", "baba[", "yaga]", "dura]","blum]", "max", "fax")

    val li = TestParser(rawJson).parse()

    System.out.println(GsonBuilder().create().toJson(li))
}

/**
 * Здесь важно следить не только за индексом, но и за глубиной.
 * Основной момент: если с нижнего уровня вернулись на уровень выше,
 * то нужно проверить разницу между глубиной текущего уровня (deep) и
 * количеством незакрытых скобок (brackets). Если deep > brackets,
 * значит на текущем уровне все элементы просмотрены и нужно
 * всплывать выше.
 *
 * NOTE: Закрывающие элементы (с тегом ]) при выходе уменьшают
 * значение brackets на количество своих закрывающих скобок.
 *
 * Теория: У каждого элемента есть голова ("code") и хвост ("view").
 * Элементы своего уровня вложенности образуют линейный массив из "голов".
 * Когда от элемента начинается "уход вниз" (открывающий элемент [), то
 * нижележащие прирастают в хвост.
 */
class TestParser(private val rawJson : List<String>) {

    private val items = mutableListOf<Item>()

    private var brackets: Int = 0
    private var index = -1

    fun parse() : List<Item> = worker(items)

    private fun worker(li: MutableList<Item>): List<Item> {

        val deep = brackets

        if(++index >= rawJson.size) return li

        val item = rawJson[index]

        when {
            item.isOpen() -> {
                brackets++
                li.add(Item(item.substringBefore("["), worker(mutableListOf())))
                return if(deep > brackets) li else worker(li)
            }
            item.isClose() -> {
                brackets -= Regex("]").findAll(item).count()
                li.add(Item(code = item.substringBefore("]")))
                return li
            }
            else -> {
                li.add(Item(code = item))
                return worker(li)
            }
        }
    }
}