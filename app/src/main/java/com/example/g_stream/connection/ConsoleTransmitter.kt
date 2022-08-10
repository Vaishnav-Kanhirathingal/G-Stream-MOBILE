package com.example.g_stream.connection

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.g_stream.connection.JoyStickControls.*
import com.example.g_stream.connection.JoyStickControls.RELEASE
import com.example.g_stream.connection.PadControls.*
import com.google.gson.Gson

/**
 * this class is to be used for control transmission to the desktop device.
 */
class ConsoleTransmitter(lifecycleOwner: LifecycleOwner) {
    // TODO: perform transmission based on parameters received
    private val TAG = this::class.java.simpleName
    private val controlLive =
        ControlLiveData(
            mouseAngle = MutableLiveData(0),
            mouseStrength = MutableLiveData(0),
            gamePad = MutableLiveData(PadControls.RELEASE),
            playerMovement = MutableLiveData(RELEASE),
            shift = MutableLiveData(false),
        )

    init {
        controlLive.apply {
            val sendStr: (padControls: PadControls) -> Unit = {
                val str = Gson().toJson(
                    Control(
                        mouseStrength = this.mouseStrength.value!!,
                        mouseAngle = this.mouseAngle.value!!,
                        gamePad = it,
                        playerMovement = this.playerMovement.value!!,
                        shift = this.shift.value!!
                    )
                )
                Log.d(TAG, "str = $str")
                // TODO: send json string to desktop
            }
            mouseAngle.observe(lifecycleOwner) { sendStr(PadControls.RELEASE) }
            mouseStrength.observe(lifecycleOwner) { sendStr(PadControls.RELEASE) }
            gamePad.observe(lifecycleOwner) { sendStr(this.gamePad.value!!) }
            playerMovement.observe(lifecycleOwner) { sendStr(PadControls.RELEASE) }
            shift.observe(lifecycleOwner) { sendStr(PadControls.RELEASE) }
        }
    }

    fun leftJoystick(joyStickControls: JoyStickControls) {
        Log.d(TAG, "leftJoystick = ${joyStickControls.name}")
        controlLive.playerMovement.value = joyStickControls
    }

    fun rightPad(padControls: PadControls) {
        Log.d(TAG, "rightJoystick = ${padControls.name}")
        controlLive.gamePad.value = padControls
    }

    fun rightJoystick(angle: Int, strength: Int) {
        Log.d(TAG, "rightPad : angle = $angle, strength = $strength")
        controlLive.apply {
            mouseAngle.value = angle
            mouseStrength.value = strength
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

data class Control(
    var mouseStrength: Int,
    var mouseAngle: Int,
    var gamePad: PadControls,
    var playerMovement: JoyStickControls,
    var shift: Boolean
)

data class ControlLiveData(
    var mouseStrength: MutableLiveData<Int>,
    var mouseAngle: MutableLiveData<Int>,
    var gamePad: MutableLiveData<PadControls>,
    var playerMovement: MutableLiveData<JoyStickControls>,
    var shift: MutableLiveData<Boolean>
)