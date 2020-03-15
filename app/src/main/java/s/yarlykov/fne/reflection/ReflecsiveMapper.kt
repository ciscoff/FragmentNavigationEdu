package s.yarlykov.fne.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
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
                        (originalValue as List<Person>).fold(0) { total, person -> total + person.id }
                    } ?: originalValue

                (it as KMutableProperty<*>).setter.call(itemOut, resultValue)
            }
        }

    return itemOut
}

/**
 * Функция работает только с теми атрибутами, которые указаны в аргументе props
 */
@Suppress("UNCHECKED_CAST")
fun ItemIn.toItemOutEx(props: Set<String>? = null): ItemOut {

    val itemOut = ItemOut()
    val kItemOut = itemOut::class

    this::class
        .declaredMemberProperties
        .filter {prop ->
            props?.let { prop.name in it } ?: true
        }.forEach { kPropIn ->

            kItemOut.declaredMemberProperties.firstOrNull { kPropOut ->
                kPropIn.name == kPropOut.name
            }?.let {

                val originalValue = (kPropIn as (KProperty1<ItemIn, Any?>)).get(this)

                val resultValue: Any? =
                    it.annotations.find {
                        it is ToInt
                    }?.let { ano ->
                        (originalValue as List<Person>).fold(0) { total, person -> total + person.id }
                    } ?: originalValue

                (it as KMutableProperty<*>).setter.call(itemOut, resultValue)
            }
        }

    return itemOut
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any, reified R : Any> toItemOutG(objIn : T, props: Set<String>? = null): R {

    val objOut = R::class.createInstance()
    val kObjOut = objOut::class

    objIn::class
        .declaredMemberProperties
        .filter {prop ->
            props?.let { prop.name in it } ?: true
        }.forEach { kPropIn ->

            kObjOut.declaredMemberProperties.firstOrNull { kPropOut ->
                kPropIn.name == kPropOut.name
            }?.let {

                val originalValue = (kPropIn as (KProperty1<T, Any?>)).get(objIn)

                val resultValue: Any? =
                    it.annotations.find {
                        it is ToInt
                    }?.let { ano ->
                        (originalValue as List<Person>).fold(0) { total, _ -> total + 1 }
                    } ?: originalValue

                (it as KMutableProperty<*>).setter.call(objOut, resultValue)
            }
        }

    return objOut
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

    println(itemIn.toItemOutEx())

    println(itemIn.toItemOutEx(setOf("propB", "person")))

    val obj = toItemOutG<ItemIn, ItemOut>(itemIn)
    println(obj.toString())
}
