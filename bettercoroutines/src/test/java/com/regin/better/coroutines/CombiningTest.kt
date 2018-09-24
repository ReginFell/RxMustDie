package com.regin.better.coroutines

import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.channels.toList
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class CombiningTest {

    @Test
    fun mergeSuccess() {
        val channel1: ReceiveChannel<Int> = GlobalScope.produce {
            (0..2).forEach {
                send(it)
                delay(TimeUnit.MILLISECONDS.toMillis(100))
            }
        }

        val channel2: ReceiveChannel<Int> = GlobalScope.produce {
            (3..4).forEach {
                send(it)
                delay(TimeUnit.MILLISECONDS.toMillis(100))
            }
        }


        runBlocking {
            val values = channel1.merge(channel2).toList()

            assert(values.containsAll(listOf(0, 1, 2, 3, 4)))
        }
    }
}