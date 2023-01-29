package com.example.g_stream.viewmodel.data

import com.example.g_stream.viewmodel.data.JoyStickControls.*
import com.example.g_stream.viewmodel.data.PadControls.*
import com.google.gson.annotations.SerializedName

/**
 * [STICK_RIGHT] - move RIGHT
 * [STICK_UP] - move UP
 * [STICK_LEFT] - move LEFT
 * [STICK_DOWN] - move DOWN
 * [RELEASE]
 * are used for joystick controls.
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
 * format - left | right pad controls
 * [TOP] - jump SPACE-BAR | Additional button TAB
 * [BOTTOM] - crouch or C | interact F
 * [CENTER] - sprinting SHIFT | LMB
 * [LEFT] - RMB | Q
 * [RIGHT] - additional button M | E
 * are used for pad controllers
 */
enum class PadControls {
    @SerializedName(value = "1")
    TOP,

    @SerializedName(value = "2")
    BOTTOM,

    @SerializedName(value = "5")
    CENTER,

    @SerializedName(value = "3")
    LEFT,

    @SerializedName(value = "4")
    RIGHT
}

/**
 * @param mouseAngle is used to specify angle of joystick
 * @param mouseStrength is used to specify how much the joystick was extended
 */
data class MouseData(
    @SerializedName(value = "1") var mouseStrength: Int,
    @SerializedName(value = "2") var mouseAngle: Int,
)