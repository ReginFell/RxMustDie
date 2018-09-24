package com.regin.better.coroutines

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

fun <E> merge(
        vararg channels: ReceiveChannel<E>,
        context: CoroutineContext = Dispatchers.Unconfined,
        block: suspend CoroutineScope.(E) -> Unit
): ReceiveChannel<E> = GlobalScope.produce(context, onCompletion = { error -> channels.all { it.cancel(error) } }) {
    val job = coroutineContext[Job]!!

    val context = coroutineContext + CoroutineExceptionHandler { _, throwable ->
        job.cancel(throwable)
    }

    channels.forEach { channel ->
        launch(context) {
            channel.consumeEach { value -> block(value) }
        }
    }
}

fun timer(
        time: Long,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        context: CoroutineContext = Dispatchers.Default,
        block: suspend CoroutineScope.() -> Unit
): ReceiveChannel<Long> = GlobalScope.produce(context) {
    delay(timeUnit.toMillis(time))
    block()
}

fun interval(
        time: Long,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        context: CoroutineContext = Dispatchers.Default,
        block: suspend CoroutineScope.() -> Unit
): ReceiveChannel<Long> = GlobalScope.produce(context) {
    while (isActive) {
        delay(timeUnit.toMillis(time))
        block()
    }
}