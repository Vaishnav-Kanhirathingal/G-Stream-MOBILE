package com.example.g_stream.viewmodel

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.g_stream.connection.ConnectionData
import com.example.g_stream.viewmodel.JoyStickControls.*
import com.example.g_stream.viewmodel.JoyStickControls.RELEASE
import com.example.g_stream.viewmodel.PadControls.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataOutputStream
import java.net.Socket

class StreamViewModel(
    lifecycleOwner: LifecycleOwner,
    private val connectionData: ConnectionData,
    private val showConnectionError: () -> Unit
) : ViewModel() {

    // TODO: perform transmission based on parameters received
    private val TAG = this::class.java.simpleName

    private var movementOutputStream: DataOutputStream? = null
    private var gamePadOutputStream: DataOutputStream? = null
    private var mouseTrackOutputStream: DataOutputStream? = null
    private var shiftOutputStream: DataOutputStream? = null

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movementOutputStream = DataOutputStream(
                    Socket(connectionData.serverIpAddress, connectionData.movementPort)
                        .getOutputStream()
                )
                gamePadOutputStream = DataOutputStream(
                    Socket(connectionData.serverIpAddress, connectionData.gamePadPort)
                        .getOutputStream()
                )
                mouseTrackOutputStream = DataOutputStream(
                    Socket(connectionData.serverIpAddress, connectionData.mouseTrackPort)
                        .getOutputStream()
                )
                shiftOutputStream = DataOutputStream(
                    Socket(connectionData.serverIpAddress, connectionData.shiftPort)
                        .getOutputStream()
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showConnectionError()
                }
                e.printStackTrace()
            }
        }
    }

    /**
     * saves value for if the shift button is pressed
     */
    fun shiftPress(pressed: Boolean) {
        Log.d(TAG, "shift pressed = $pressed")
        scope.launch {
            shiftOutputStream?.apply { writeUTF(Gson().toJson(pressed));flush() }
        }
    }

    /**
     * sends the raw values for mouse controls
     */
    fun leftJoystick(joyStickControls: JoyStickControls) {
        Log.d(TAG, "value received = $joyStickControls")
        scope.launch {
            movementOutputStream?.apply { writeUTF(Gson().toJson(joyStickControls));flush() }
        }
    }

    /**
     * sends pad button press values
     */
    fun rightPad(padControls: PadControls) {
        Log.d(TAG, "rightJoystick = ${padControls.name}")
        scope.launch {
            gamePadOutputStream?.apply { writeUTF(Gson().toJson(padControls));flush() }
        }
    }

    /**
     * used to send mouse pointer values to the desktop
     */
    fun rightJoystick(angle: Int, strength: Int) {
        Log.d(TAG, "rightPad : angle = $angle, strength = $strength")
        scope.launch {
            mouseTrackOutputStream?.apply {
                writeUTF(Gson().toJson(MouseData(mouseStrength = strength, mouseAngle = angle)))
                flush()
            }
        }
    }
}

/**
 * [STICK_RIGHT], [STICK_UP], [STICK_LEFT], [STICK_DOWN], [RELEASE] are used for joystick controls.
 * These are common for both the left and right controls.
 */
enum class JoyStickControls {
    STICK_RIGHT, STICK_UP, STICK_LEFT, STICK_DOWN, RELEASE
}

/**
 * [TRIANGLE], [SQUARE], [CIRCLE], [CROSS] are used for pad controllers
 */
enum class PadControls {
    TRIANGLE, SQUARE, CIRCLE, CROSS, RELEASE
}

data class MouseData(
    var mouseStrength: Int,
    var mouseAngle: Int,
)