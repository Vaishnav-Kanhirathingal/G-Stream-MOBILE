package com.example.g_stream.viewmodel.data

import com.example.g_stream.viewmodel.data.JoyStickControls.*
import com.example.g_stream.viewmodel.data.PadControls.*
import com.google.gson.annotations.SerializedName

/**
 * [STICK_RIGHT] - move RIGHT
 * [STICK_UP] - move UP
 * [STICK_LEFT] - move LEFT
 * [STICK_DOWN] - move DOWN
 * [RELEASE] - release button
 * [STICK_UP_RIGHT] - move UP RIGHT
 * [STICK_UP_LEFT] - move UP LEFT
 * [STICK_DOWN_RIGHT] - move DOWN RIGHT
 * [STICK_DOWN_LEFT] - move DOWN LEFT
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
    RELEASE,

    @SerializedName(value = "6")
    STICK_UP_RIGHT,

    @SerializedName(value = "7")
    STICK_UP_LEFT,

    @SerializedName(value = "8")
    STICK_DOWN_RIGHT,

    @SerializedName(value = "9")
    STICK_DOWN_LEFT,
}

/**
 * format - left | right pad controls
 * [TOP_PRESSED] - jump SPACE-BAR | Additional button TAB
 * [BOTTOM_PRESSED] - crouch or C | interact F
 * [CENTER_PRESSED] - sprinting SHIFT | LMB
 * [LEFT_PRESSED] - RMB | Q
 * [RIGHT_PRESSED] - additional button M | E
 * [TOP_RELEASED], [BOTTOM_RELEASED], [CENTER_RELEASED], [LEFT_RELEASED], [RIGHT_RELEASED] - release respective buttons
 * are used for pad controllers
 */
enum class PadControls {
    @SerializedName(value = "1")
    TOP_PRESSED,

    @SerializedName(value = "2")
    TOP_RELEASED,

    @SerializedName(value = "3")
    BOTTOM_PRESSED,

    @SerializedName(value = "4")
    BOTTOM_RELEASED,

    @SerializedName(value = "5")
    CENTER_PRESSED,

    @SerializedName(value = "6")
    CENTER_RELEASED,

    @SerializedName(value = "7")
    LEFT_PRESSED,

    @SerializedName(value = "8")
    LEFT_RELEASED,

    @SerializedName(value = "9")
    RIGHT_PRESSED,

    @SerializedName(value = "10")
    RIGHT_RELEASED
}

/**
 * @param mouseMovementX is used to specify coordinate X
 * @param mouseMovementY is used to specify coordinate Y
 */
data class MouseData(
    @SerializedName(value = "1") var mouseMovementX: Int,
    @SerializedName(value = "2") var mouseMovementY: Int,
)