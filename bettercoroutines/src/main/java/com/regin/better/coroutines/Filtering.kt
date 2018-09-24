package com.regin.better.coroutines

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.timeunit.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

fun <E> ReceiveChannel<E>.debounce(
        time: Long,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        context: CoroutineContext = Dispatchers.Unconfined
): ReceiveChannel<E> = produce(context) {
    var job: Job? = null
    consumeEach {
        job?.cancel()
        job = launch {
            delay(timeUnit.toMillis(time))
            send(it)
        }
    }
    job?.join()

}

fun <E> ReceiveChannel<E>.distinctUntilChanged(): ReceiveChannel<E> = GlobalScope.produce(Dispatchers.Unconfined) {

    var lastItem: E? = null

    consumeEach {
        if (lastItem != it) {
            lastItem = it
            send(it)
        }
    }
}