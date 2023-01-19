package com.example.g_stream.viewmodel.data

import com.example.g_stream.viewmodel.data.JoyStickControls.*
import com.example.g_stream.viewmodel.data.JoyStickControls.RELEASE
import com.example.g_stream.viewmodel.data.PadControls.*
import com.google.gson.annotations.SerializedName

/**
 * [STICK_RIGHT], [STICK_UP], [STICK_LEFT], [STICK_DOWN], [RELEASE] are used for joystick controls.
 * These are common for both the left and right controls.
 */
enum class JoyStickControls {
    @SerializedName(value = "1")
    STICK_RIGHT,

    @SerializedName(value = "2")
    STICK_UP,

    @SerializedName(value = "3")
    STICK_LEFT,

    @SerializedName(value = "4")
    STICK_DOWN,

    @SerializedName(value = "5")
    RELEASE
}

/**
 * [TRIANGLE], [SQUARE], [CIRCLE], [CROSS] are used for pad controllers
 */
enum class PadControls {
    @SerializedName(value = "1")
    TRIANGLE,

    @SerializedName(value = "2")
    SQUARE,

    @SerializedName(value = "3")
    CIRCLE,

    @SerializedName(value = "4")
    CROSS,

    @SerializedName(value = "5")
    RELEASE
}

data class MouseData(
    @SerializedName(value = "1")
    var mouseStrength: Int,
    @SerializedName(value = "2")
    var mouseAngle: Int,
)