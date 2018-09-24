package com.regin.better.coroutines.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.regin.better.coroutines.debounce
import com.regin.better.coroutines.sample.widget.clicks
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val clicks = debounceShowcase.clicks().openSubscription()
                .debounce(500, TimeUnit.MILLISECONDS)

        GlobalScope.launch {
            clicks.consumeEach {
                Log.d("Result", "click")
            }
        }
    }
}