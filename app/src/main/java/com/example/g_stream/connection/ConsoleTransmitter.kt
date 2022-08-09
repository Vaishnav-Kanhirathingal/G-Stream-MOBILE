package com.example.g_stream.connection

import android.util.Log
import com.example.g_stream.connection.JoyStickControls.*
import com.example.g_stream.connection.PadControls.*

/**
 * this class is to be used for control transmission to the desktop device.
 */
class ConsoleTransmitter {
    // TODO: perform transmission based on parameters received
    val TAG = this::class.java.simpleName

    fun leftJoystick(joyStickControls: JoyStickControls) {
        Log.d(TAG, "leftJoystick = ${joyStickControls.name}")
    }

    fun rightPad(padControls: PadControls) {
        Log.d(TAG, "rightJoystick = ${padControls.name}")
    }

    fun rightJoystick(angle: Int, strength: Int) {
        // TODO: use direction while transmitting for mouse accuracy
        Log.d(TAG, "rightPad : angle = $angle, strength = $strength")
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
    TRIANGLE, SQUARE, CIRCLE, CROSS,
}