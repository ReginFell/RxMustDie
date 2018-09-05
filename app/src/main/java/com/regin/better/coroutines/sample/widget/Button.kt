package com.regin.better.coroutines.sample.widget

import android.view.View
import android.widget.Button
import kotlinx.coroutines.experimental.channels.BroadcastChannel
import kotlinx.coroutines.experimental.channels.broadcast
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.suspendCoroutine

fun Button.clicks(): BroadcastChannel<View> {
    return broadcast {
        suspendCoroutine {
            setOnClickListener { view ->
                launch { send(view) }
            }
        }
    }
}