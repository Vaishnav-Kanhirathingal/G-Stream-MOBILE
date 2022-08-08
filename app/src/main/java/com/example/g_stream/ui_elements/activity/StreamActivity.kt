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
import com.example.g_stream.connection.ConsoleTransmitter
import com.example.g_stream.connection.ConsoleTransmitterEnum
import com.example.g_stream.databinding.ActivityStreamBinding
import com.google.gson.Gson

class StreamActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    private val strengthLimit = 40
    private val transmitter = ConsoleTransmitter()

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

    private fun applyLeftSectionBinding() {
        binding.apply {
            leftJoystick.setOnMoveListener { angle, strength ->
                transmitter.leftJoystick(
                    if (strength <= strengthLimit) {
                        ConsoleTransmitterEnum.RELEASE
                    } else if ((angle > 315 || angle <= 45) && strength > strengthLimit) {
                        ConsoleTransmitterEnum.STICK_RIGHT
                    } else if ((angle in 46..135) && strength > strengthLimit) {
                        ConsoleTransmitterEnum.STICK_UP
                    } else if ((angle in 136..225) && strength > strengthLimit) {
                        ConsoleTransmitterEnum.STICK_LEFT
                    } else if ((angle in 226..315) && strength > strengthLimit) {
                        ConsoleTransmitterEnum.STICK_DOWN
                    } else {
                        ConsoleTransmitterEnum.RELEASE
                    }
                )
            }
        }
    }

    private fun applyRightSectionBinding() {
        binding.apply {
            triangleButton.setOnClickListener { transmitter.rightPad(ConsoleTransmitterEnum.TRIANGLE) }
            squareButton.setOnClickListener { transmitter.rightPad(ConsoleTransmitterEnum.SQUARE) }
            circleButton.setOnClickListener { transmitter.rightPad(ConsoleTransmitterEnum.CIRCLE) }
            crossButton.setOnClickListener { transmitter.rightPad(ConsoleTransmitterEnum.CROSS) }
            rightJoystick.setOnMoveListener { angle, strength ->
                transmitter.rightJoystick(angle, strength)
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

    override fun onResume() {
        super.onResume()
        goFullScreen()
    }
}