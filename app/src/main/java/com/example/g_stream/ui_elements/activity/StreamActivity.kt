package com.example.g_stream.ui_elements.activity

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.g_stream.R
import com.example.g_stream.connection.ConnectionData
import com.example.g_stream.connection.ConsoleTransmitter
import com.example.g_stream.connection.JoyStickControls
import com.example.g_stream.connection.PadControls
import com.example.g_stream.databinding.ActivityStreamBinding
import com.google.gson.Gson

class StreamActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    private val strengthLimit = 40
    private val transmitter = ConsoleTransmitter(this)
    private val shiftActive = MutableLiveData(false)

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
            Log.d(TAG, "data received = ${it!!}")
            return Gson().fromJson(it, ConnectionData::class.java)
        }
    }

    private fun applyBinding() {
        applyLeftSectionBinding()
        applyRightSectionBinding()
    }

    /**
     * this function performs left section bindings. Joystick is divided into four sections which
     * are implementations of the UP, DOWN, LEFT and RIGHT button in the desktop keyboard.
     */
    private fun applyLeftSectionBinding() {
        binding.apply {
            shiftImageButton.setOnClickListener { shiftActive.value = !shiftActive.value!! }
            shiftActive.observe(this@StreamActivity) {
                transmitter.shiftPress(it)
                shiftActiveImageView.setImageResource(if (it) R.color.green else R.color.red)
            }

            var control = JoyStickControls.RELEASE
            leftJoystick.setOnMoveListener { angle, strength ->
                val temp =
                    if (strength <= strengthLimit) {
                        JoyStickControls.RELEASE
                    } else {
                        when (angle) {
                            in 46..135 -> JoyStickControls.STICK_UP
                            in 136..225 -> JoyStickControls.STICK_LEFT
                            in 226..315 -> JoyStickControls.STICK_DOWN
                            else -> JoyStickControls.STICK_RIGHT
                        }
                    }
                if (temp != control) {
                    control = temp
                    transmitter.leftJoystick(control)
                }
            }
        }
    }

    /**
     * this applies binding to the right section of the screen. This includes the game pad controls
     * which include the TRIANGLE, SQUARE, CIRCLE and the CROSS buttons and the joystick controls.
     * Joystick controls send the raw data directly to the transmitter for better accuracy.
     */
    private fun applyRightSectionBinding() {
        binding.apply {
            triangleButton.setOnClickListener { transmitter.rightPad(PadControls.TRIANGLE) }
            squareButton.setOnClickListener { transmitter.rightPad(PadControls.SQUARE) }
            circleButton.setOnClickListener { transmitter.rightPad(PadControls.CIRCLE) }
            crossButton.setOnClickListener { transmitter.rightPad(PadControls.CROSS) }
            rightJoystick.setOnMoveListener { angle, strength ->
                transmitter.rightJoystick(angle, strength)
            }
        }
    }

    /**
     * makes the activity go full screen.
     */
    private fun goFullScreen() {
        // TODO: check if usability of this function.
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