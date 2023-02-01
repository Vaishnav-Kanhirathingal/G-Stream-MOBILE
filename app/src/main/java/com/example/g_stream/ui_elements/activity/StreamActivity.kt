package com.example.g_stream.ui_elements.activity

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.g_stream.connection.ConnectionData
import com.example.g_stream.databinding.ActivityStreamBinding
import com.example.g_stream.viewmodel.StreamViewModel
import com.example.g_stream.viewmodel.data.JoyStickControls
import com.example.g_stream.viewmodel.data.PadControls
import com.google.gson.Gson

class StreamActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    private val strengthLimit = 60
    private lateinit var viewModel: StreamViewModel

    private lateinit var binding: ActivityStreamBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStreamBinding.inflate(layoutInflater)
        viewModel = StreamViewModel(
            connectionData = getConnectionDataFromIntent(),
            showConnectionError = {
                Toast.makeText(this, "Error initiating a connection", Toast.LENGTH_SHORT).show()
                AlertDialog
                    .Builder(this)
                    .setMessage("Error initiating a connection, return back to scan screen and re-initiate a connection?")
                    .setPositiveButton("yes") { _, _ -> finish() }
                    .setNegativeButton("no") { _, _ -> }
                    .show()
            },
            audioWarning = { binding.audioWarning.setBackgroundColor(Color.RED) },
            videoWarning = { binding.videoWarning.setBackgroundColor(Color.RED) },
            leftJoystickWarning = { binding.leftJoystickWarning.setBackgroundColor(Color.RED) },
            rightJoystickWarning = { binding.rightJoystickWarning.setBackgroundColor(Color.RED) },
            leftGamePadWarning = { binding.leftGamePadWarning.setBackgroundColor(Color.RED) },
            rightGamePadWarning = { binding.rightGamePadWarning.setBackgroundColor(Color.RED) },
        )
        setContentView(binding.root)
        applyBinding()
    }

    private fun getConnectionDataFromIntent(): ConnectionData {
        intent.getStringExtra(ConnectionData.key).let {
            Log.d(TAG, "data received = ${it!!}")
            return Gson().fromJson(it, ConnectionData::class.java)
        }
    }

    private fun applyBinding() {
        applyLeftSectionBinding()
        applyRightSectionBinding()
        applyStreamBinding()
        goFullScreen()
    }

    private fun applyStreamBinding() {
        viewModel.startVideoStreaming { jpegImageByteArray: ByteArray ->
            try {
                val bitmap = BitmapFactory.decodeByteArray(
                    jpegImageByteArray,
                    0,
                    jpegImageByteArray.size
                )
                binding.testImageView.setImageBitmap(bitmap)
                Log.d(TAG, "image set")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        viewModel.startAudioStreaming()
    }

    /**
     * this function performs left section bindings. Joystick is divided into four sections which
     * are implementations of the UP, DOWN, LEFT and RIGHT button in the desktop keyboard.
     */
    private fun applyLeftSectionBinding() {
        binding.apply {
            topLpButton.setOnClickListener { viewModel.leftPad(PadControls.TOP) }
            leftLpButton.setOnClickListener { viewModel.leftPad(PadControls.LEFT) }
            rightLpButton.setOnClickListener { viewModel.leftPad(PadControls.RIGHT) }
            bottomLpButton.setOnClickListener { viewModel.leftPad(PadControls.BOTTOM) }
            centerLPButton.setOnClickListener { viewModel.leftPad(PadControls.CENTER) }

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
                    viewModel.leftJoystick(control)
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
            topRpButton.setOnClickListener { viewModel.rightPad(PadControls.TOP) }
            leftRpButton.setOnClickListener { viewModel.rightPad(PadControls.LEFT) }
            rightRpButton.setOnClickListener { viewModel.rightPad(PadControls.RIGHT) }
            bottomRpButton.setOnClickListener { viewModel.rightPad(PadControls.BOTTOM) }
            centerRPButton.setOnClickListener { viewModel.rightPad(PadControls.CENTER) }
            rightJoystick.setOnMoveListener(
                { angle, strength -> viewModel.rightJoystick(angle, strength) },
                20
            )
        }
    }

    /**
     * hides the navigation bar
     */
    private fun goFullScreen() {
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
    }

    override fun onResume() {
        super.onResume()
        goFullScreen()
    }
}