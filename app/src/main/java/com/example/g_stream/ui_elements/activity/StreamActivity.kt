package com.example.g_stream.ui_elements.activity

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import com.example.g_stream.connection.ConnectionData
import com.example.g_stream.databinding.ActivityStreamBinding
import com.google.gson.Gson

class StreamActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName

    private lateinit var binding: ActivityStreamBinding
    private val hideHandler = Handler(Looper.myLooper()!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val connectionData: ConnectionData = getFromIntent()

        binding = ActivityStreamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyBinding()
    }

    private fun getFromIntent(): ConnectionData {
        intent.getStringExtra(ConnectionData.key).let {
            Log.d(TAG, "data received = $it!!")
            return Gson().fromJson(it, ConnectionData::class.java)
        }
    }

    private fun applyBinding() {
        goFullScreen()
        applyLeftSectionBinding()
        applyRightSectionBinding()
    }

    private fun applyRightSectionBinding() {
        binding.apply {
            upButton.setOnClickListener { Log.d(TAG, "upButton pressed") }
            leftButton.setOnClickListener { Log.d(TAG, "leftButton pressed") }
            rightButton.setOnClickListener { Log.d(TAG, "rightButton pressed") }
            downButton.setOnClickListener { Log.d(TAG, "downButton pressed") }
        }
    }

    private fun applyLeftSectionBinding() {
        binding.apply {
            triangleButton.setOnClickListener { Log.d(TAG, "triangleButton pressed") }
            squareButton.setOnClickListener { Log.d(TAG, "squareButton pressed") }
            circleButton.setOnClickListener { Log.d(TAG, "circleButton pressed") }
            crossButton.setOnClickListener { Log.d(TAG, "crossButton pressed") }
            rightJoystick.setOnMoveListener { angle, strength ->
                Log.d(TAG, "values received : angle = $angle and strength = $strength")
            }
        }
    }

    private fun goFullScreen() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.hide()
        hideHandler.removeCallbacks { supportActionBar?.show() }
        hideHandler.postDelayed({
            if (Build.VERSION.SDK_INT >= 30) {
                binding.fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            } else {
                binding.fullscreenContent.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LOW_PROFILE or
                            View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }
        }, 300L)
    }
}