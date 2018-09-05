package com.regin.better.coroutines.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.regin.better.coroutines.debounce
import com.regin.better.coroutines.sample.widget.clicks
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val clicks = debounceShowcase.clicks().openSubscription()
                .debounce(500, TimeUnit.MILLISECONDS)

        launch {
            clicks.consumeEach {
                Log.d("Result", "click")
            }
        }
    }
}