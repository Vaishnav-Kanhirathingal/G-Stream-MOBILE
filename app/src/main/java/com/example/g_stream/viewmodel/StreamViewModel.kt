package com.example.g_stream.viewmodel

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
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
    private val lifecycleOwner: LifecycleOwner,
    private val connectionData: ConnectionData,
    private val showConnectionError: () -> Unit
) : ViewModel() {

    // TODO: perform transmission based on parameters received
    private val TAG = this::class.java.simpleName
    private val controlLive =
        ControlLive(
            mouseData = MutableLiveData(MouseData(0, 0)),
            gamePad = MutableLiveData(PadControls.RELEASE),
            playerMovement = MutableLiveData(JoyStickControls.RELEASE),
            shift = MutableLiveData(false),
        )

    private var controlSocket: Socket? = null
    private var controlOutputStream: DataOutputStream? = null

    // TODO: switch to separate thread

    init {
        controlLive.apply {
            val sendStr: (PadControls) -> Unit = {
                val str = Gson().toJson(
                    Control(
                        mouseData = this.mouseData.value!!,
                        gamePad = it,
                        playerMovement = this.playerMovement.value!!,
                        shift = this.shift.value!!
                    )
                )
                sendString(str)
                // TODO: send json string [str] to desktop
            }
            mouseData.observe(lifecycleOwner) { sendStr(PadControls.RELEASE) }
            gamePad.observe(lifecycleOwner) { sendStr(this.gamePad.value!!) }
            playerMovement.observe(lifecycleOwner) { sendStr(PadControls.RELEASE) }
            shift.observe(lifecycleOwner) { sendStr(PadControls.RELEASE) }
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                controlSocket = Socket(connectionData.serverIpAddress, connectionData.controlPort)
                controlOutputStream = DataOutputStream(controlSocket?.getOutputStream())
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showConnectionError()
                }
                e.printStackTrace()
            }
        }
    }

    private fun sendString(str: String) {
        Log.d(TAG, "json str = $str, connection status = ${controlSocket?.isConnected}")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                controlOutputStream?.apply { writeUTF(str);flush() }
            } catch (e: Exception) {
                Log.e(TAG, "error = ${e.message}")
            }
        }
    }


    /**
     * saves value for if the shift button is pressed
     */
    fun shiftPress(pressed: Boolean) {
        Log.d(TAG, "shift pressed = $pressed")
        controlLive.shift.value = pressed
    }

    /**
     * sends the raw values for mouse controls
     */
    fun leftJoystick(joyStickControls: JoyStickControls) {
        Log.d(
            TAG, "value received = $joyStickControls, old = ${controlLive.playerMovement.value}"
        )
        controlLive.playerMovement.value = joyStickControls
    }

    /**
     * sends pad button press values
     */
    fun rightPad(padControls: PadControls) {
        Log.d(TAG, "rightJoystick = ${padControls.name}")
        controlLive.gamePad.value = padControls
    }

    /**
     * used to send mouse pointer values to the desktop
     */
    fun rightJoystick(angle: Int, strength: Int) {
        Log.d(TAG, "rightPad : angle = $angle, strength = $strength")
        MouseData(
            mouseStrength = strength, mouseAngle = angle
        ).apply {
            controlLive.mouseData.value = this
        }
        // TODO: should be a single variable change
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

/**
 * used to create json values to be sent to the desktop
 */
data class Control(
    var mouseData: MouseData,
    var gamePad: PadControls,
    var playerMovement: JoyStickControls,
    var shift: Boolean
)

/**
 * stores live data values for controls
 */
data class ControlLive(
    var mouseData: MutableLiveData<MouseData>,
    var gamePad: MutableLiveData<PadControls>,
    var playerMovement: MutableLiveData<JoyStickControls>,
    var shift: MutableLiveData<Boolean>
)

data class MouseData(
    var mouseStrength: Int,
    var mouseAngle: Int,
)