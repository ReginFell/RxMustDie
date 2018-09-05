package com.regin.better.coroutines

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.coroutineContext

class Channels {

    companion object {
        fun <E> merge(
                vararg channels: ReceiveChannel<E>,
                context: CoroutineContext = Unconfined
        ): ReceiveChannel<E> = produce(context, onCompletion = { error -> channels.all { it.cancel(error) } }) {
            val job = coroutineContext[Job]!!

            val context = coroutineContext + CoroutineExceptionHandler { _, throwable ->
                job.cancel(throwable)
            }

            channels.forEach { channel ->
                launch(context) {
                    channel.consumeEach { value -> send(value) }
                }
            }
        }

        fun timer(
                time: Long,
                timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
                context: CoroutineContext = DefaultDispatcher
        ): ReceiveChannel<Long> = produce(context) {
            delay(timeUnit.toMillis(time))
            send(0L)
        }
    }
}