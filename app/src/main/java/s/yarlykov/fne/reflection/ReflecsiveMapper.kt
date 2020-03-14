package s.yarlykov.fne.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

data class Person(val id: Int, val name: String)


class ItemIn {
    var propA: Int? = null
    var propB: Double? = 0.0
    var person: List<Person>? = null
}

class ItemOut {
    var propA: Int? = null
    var propB: Double? = null

    @ToInt
    var person: Int? = null

    override fun toString(): String {
        return "$propA, $propB, $person"
    }
}

@Suppress("UNCHECKED_CAST")
fun ItemIn.toItemOut(): ItemOut {

    val itemOut = ItemOut()
    val kItemOut = itemOut::class

    this::class.declaredMemberProperties
        .forEach { kPropIn ->

            kItemOut.declaredMemberProperties.firstOrNull { kPropOut ->
                kPropIn.name == kPropOut.name
            }?.let {

                val originalValue = (kPropIn as (KProperty1<ItemIn, Any?>)).get(this)

                val resultValue: Any? =
                    it.annotations.find {
                        it is ToInt
                    }?.let { ano ->
                        (originalValue as List<Person>).fold(0){total, person -> total + person.id}
                    } ?: originalValue

                (it as KMutableProperty<*>).setter.call(itemOut, resultValue)
            }
        }

    return itemOut
}


fun <T : Any> construct(kClass: KClass<T>): T? {
    val ctor = kClass.primaryConstructor
    return if (ctor != null && ctor.parameters.isEmpty())
        ctor.call() else
        null
}

fun main(args: Array<String>) {

    val persons = listOf(Person(1, "Anna"), Person(2, "Kate"), Person(3, "Papa"))

    val itemIn = ItemIn().apply {
        propA = 1
        propB = 2.0
        person = persons
    }

    // Получить KClass<ItemIn> из инстанса ItemIn
//    val kItemIn = itemIn.javaClass.kotlin
//    println(kItemIn.simpleName)
//    kItemIn.memberProperties.forEach { println(it.name) }

    (0..100000).forEach {
        println("$it: " + itemIn.toItemOut())
    }


}
