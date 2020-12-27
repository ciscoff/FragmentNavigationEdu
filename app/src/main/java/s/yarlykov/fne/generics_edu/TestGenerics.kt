package s.yarlykov.fne.generics_edu

interface Group<T> {
    fun fetch() : T
    fun put(data : T)
}

open class Animal
class Cat : Animal()

/**
 * Обрати внимание, что * ставится в месте где Group применяется, а не в месте декларации
 * интерфейса. Там просто стоит Т. Установив * в месте использования, мы фактически генерим
 * новый тип (помни, что тип - это не класс). И этот тип выглядит так:
 *
 * interface Group {
 *   fun insert(item: Nothing): Unit
 *   fun fetch(): Any?
 * }
 *
 */

interface SuperGroup {
    fun insert(data : Nothing)
    fun fetch(): Any?
}

/**
 * Когда подтип переопределяет метод и указывает аргумент другим типом (в том числе и дженериком),
 * то kotlin это запрещает потому что возникает и должна соблюдаться контрвариантность (принимать
 * нужно не меньше,чем родитель). Даже если все правильно указать (как в примере ниже), то
 * компилироваться не будет. Это потому, что нужно поддерживать не только override но и overload
 * функции.
 */
interface SuperGroupChild<T : Animal> : SuperGroup {
    // Нельзя этот метод переопределить, потому что kotlin запрещает контрвариантные аргументы.
//    override fun insert(data: T)
//    override fun insert(data: Animal)

    // А это overload. Здесь все ОК
    fun insert(data: Animal)
    override fun fetch(): T
}

fun useSuperGroup(group : SuperGroup) {
    println("group=${group::class.java.simpleName}")
}

fun useGroup(group : Group<*>) {
    println("group=${group::class.java.simpleName}")
}

val objAnimal = object : Group<Animal> {
    override fun fetch(): Animal {
        return Animal()
    }
    override fun put(data: Animal) {
    }
}

val objCat = object : Group<Cat> {
    override fun fetch(): Cat {
        return Cat()
    }

    override fun put(data: Cat) {
    }
}

val objSuperAnimal = object : SuperGroup {
    override fun insert(item: Nothing) {
    }

    override fun fetch(): Any? {
        return null
    }
}

val superGroupChild = object : SuperGroupChild<Animal> {
    override fun insert(data: Nothing) {
    }

    // Это тот метод, который был overload в SuperGroupChild
    override fun insert(data: Animal) {
    }

    // Обрати внимание, что return - ковариантный. Type parameter - Animal, а
    // функция возвращает Cat'a.
    override fun fetch(): Cat {
        return Cat()
    }
}


fun main(args : Array<String>) {
    useGroup(objCat)
    useGroup(objAnimal)

    useSuperGroup(objSuperAnimal)
    useSuperGroup(superGroupChild)
}