package com.regin.better.coroutines

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.channels.toList
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class TransformingTest {

    @Test
    fun bufferWithCount() {
        val channel: ReceiveChannel<Int> = produce {
            (0..21).forEach {
                send(it)
                delay(TimeUnit.MILLISECONDS.toMillis(100))
            }
        }

        runBlocking {
            val values = channel.buffer(10).toList()

            assert(values.size == 2)
            assert(values[0] == listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
            assert(values[1] == listOf(10, 11, 12, 13, 14, 15, 16, 17, 18, 19))

        }
    }

    @Test
    fun bufferWithTime() {
        val channel: ReceiveChannel<Int> = produce {
            (0..10).forEach {
                delay(TimeUnit.MILLISECONDS.toMillis(150))
                send(it)
            }
        }

        runBlocking {
            val values = channel.buffer(420, TimeUnit.MILLISECONDS).toList()

            assert(values.size == 3)
            assert(values[0] == listOf(0, 1, 2))
            assert(values[1] == listOf(3, 4, 5))
            assert(values[2] == listOf(6, 7, 8))
        }
    }
}