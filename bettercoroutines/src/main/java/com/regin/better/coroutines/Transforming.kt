package com.regin.better.coroutines

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

fun <E> ReceiveChannel<E>.buffer(
        count: Int,
        context: CoroutineContext = Unconfined
): ReceiveChannel<List<E>> = produce(context) {

    val items: ArrayList<E> = ArrayList(count)

    consumeEach {
        items.add(it)
        if (items.size >= count) {
            send(items.toList())
            items.clear()
        }
    }
}

fun <E> ReceiveChannel<E>.buffer(
        time: Long,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        context: CoroutineContext = Unconfined
): ReceiveChannel<List<E>> = produce(context) {

    val items: ArrayList<E> = ArrayList()
    var nextTime = 0L

    consumeEach {
        val currentTime = System.currentTimeMillis()

        if (currentTime > nextTime) {
            nextTime = currentTime + timeUnit.toMillis(time)
            launch {
                delay(timeUnit.toMillis(time))
                send(items.toList())
                items.clear()
            }
        }
        items.add(it)
    }
}