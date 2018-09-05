package com.regin.better.coroutines

import kotlinx.coroutines.experimental.channels.*
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class FilteringTest {

    @Test
    fun debounce() {
        val channel: ReceiveChannel<Int> = produce {
            (0..15).forEach {
                send(it)
                delay(TimeUnit.MILLISECONDS.toMillis(100))
            }
        }


        runBlocking {
            val values = channel.debounce(110, TimeUnit.MILLISECONDS)
                    .toList()

            assert(values.size == 1)
            assert(values[0] == 15)

        }
    }

    @Test
    fun distinctUntilChanged() {
        val channel: ReceiveChannel<Int> = produce {
            listOf(1, 1, 0, 0, 1, 2, 2, 0).forEach {
                send(it)
            }
        }

        runBlocking {
            val values = channel.distinctUntilChanged().toList()

            assert(values.size == 5)
            assert(values == listOf(1, 0, 1, 2, 0))
        }
    }
}