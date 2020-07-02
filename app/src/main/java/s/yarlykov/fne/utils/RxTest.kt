package s.yarlykov.fne.utils

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun main(arg: Array<String>) {

    val initialDelay = 5000L

    val charsObs = Observable.zip(
        Observable.fromIterable('A'..'Z'),
        Observable.interval(initialDelay, 2000, TimeUnit.MILLISECONDS),
        BiFunction<Char, Long, Char> { t1, _ ->
            t1
        }
    )

    val countObs = Observable.interval(0, 500, TimeUnit.MILLISECONDS)

    println("Waiting for charsObs to start emission... (it takes $initialDelay ms)")

    val d = countObs.withLatestFrom(
        charsObs,
        BiFunction<Long, Char, Pair<Long, Char>> { i, ch ->
            i to ch
        })
        .subscribeOn(Schedulers.io())
        .subscribe (
            {
                val (count, ch) = it
                println("count=$count, ch=$ch")
            }, {_ -> println("exception has occurred")}
        )

    Thread.sleep(100000)
}