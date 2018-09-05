package com.regin.better.coroutines

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.coroutineContext

fun <E> ReceiveChannel<E>.merge(
        vararg channels: ReceiveChannel<E>,
        context: CoroutineContext = Unconfined
): ReceiveChannel<E> = produce(context, onCompletion = { error -> channels.all { it.cancel(error) } }) {
    val job = coroutineContext[Job]!!

    val context = coroutineContext + CoroutineExceptionHandler { _, throwable ->
        job.cancel(throwable)
    }

    consumeEach {
        send(it)

        channels.forEach { channel ->
            launch(context) {
                channel.consumeEach { e -> send(e) }
            }
        }
    }
}
