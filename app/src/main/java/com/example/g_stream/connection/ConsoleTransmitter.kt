package com.example.g_stream.connection

import android.util.Log

class ConsoleTransmitter {
    // TODO: perform transmission based on parameters received
    val TAG = this::class.java.simpleName

    fun leftJoystick(consoleTransmitterEnum: ConsoleTransmitterEnum) {
        Log.d(TAG, "leftJoystick = ${consoleTransmitterEnum.name}")
    }

    fun rightPad(consoleTransmitterEnum: ConsoleTransmitterEnum) {
        Log.d(TAG, "rightJoystick = ${consoleTransmitterEnum.name}")
    }

    fun rightJoystick(angle: Int, strength: Int) {
        // TODO: use direction while transmitting for mouse accuracy
        Log.d(TAG, "rightPad : angle = $angle, strength = $strength")
    }
}

enum class ConsoleTransmitterEnum {
    TRIANGLE, SQUARE, CIRCLE, CROSS,
    STICK_RIGHT, STICK_UP, STICK_LEFT, STICK_DOWN, RELEASE

}