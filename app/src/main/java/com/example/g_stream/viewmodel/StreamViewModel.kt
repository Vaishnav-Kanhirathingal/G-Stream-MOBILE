package com.example.g_stream.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.g_stream.connection.ConnectionData
import com.example.g_stream.viewmodel.data.JoyStickControls
import com.example.g_stream.viewmodel.data.MouseData
import com.example.g_stream.viewmodel.data.PadControls
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

class StreamViewModel(
    private val connectionData: ConnectionData,
    private val showConnectionError: () -> Unit,
    private val audioWarning: () -> Unit,
    private val videoWarning: () -> Unit,
    private val leftJoystickWarning: () -> Unit,
    private val rightJoystickWarning: () -> Unit,
    private val leftGamePadWarning: () -> Unit,
    private val rightGamePadWarning: () -> Unit,
) : ViewModel() {
    private val TAG = this::class.java.simpleName

    private var movementOutputStream: DataOutputStream? = null
    private var gamePadOutputStream: DataOutputStream? = null
    private var mouseTrackOutputStream: DataOutputStream? = null
    private var screenStream: DataInputStream? = null
    private var leftGamePadStream: DataOutputStream? = null

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            try {
                movementOutputStream = DataOutputStream(
                    Socket(connectionData.serverIpAddress, connectionData.leftJoyStickPort)
                        .getOutputStream()
                )
                gamePadOutputStream = DataOutputStream(
                    Socket(connectionData.serverIpAddress, connectionData.rightGamePadPort)
                        .getOutputStream()
                )
                mouseTrackOutputStream = DataOutputStream(
                    Socket(connectionData.serverIpAddress, connectionData.rightJoyStickPort)
                        .getOutputStream()
                )
                screenStream = DataInputStream(
                    Socket(connectionData.serverIpAddress, connectionData.videoPort)
                        .getInputStream()
                )
                leftGamePadStream = DataOutputStream(
                    Socket(connectionData.serverIpAddress, connectionData.leftGamePadPort)
                        .getOutputStream()
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { showConnectionError() }
                e.printStackTrace()
            }
        }
    }

    /**
     * sends pad button press values
     */
    fun leftPad(padControls: PadControls) {
        Log.d(TAG, "value received = $padControls")
        scope.launch {
            try {
                leftGamePadStream?.apply { writeUTF(Gson().toJson(padControls));flush() }
            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "error for leftPad")
                leftGamePadWarning()
            }
        }
    }

    /**
     * sends the raw values for mouse controls
     */
    fun leftJoystick(joyStickControls: JoyStickControls) {
        Log.d(TAG, "value received = $joyStickControls")
        scope.launch {
            try {
                movementOutputStream?.apply { writeUTF(Gson().toJson(joyStickControls));flush() }
            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "error for leftJoystick")
                leftJoystickWarning()
            }
        }
    }

    /**
     * sends pad button press values
     */
    fun rightPad(padControls: PadControls) {
        Log.d(TAG, "rightJoystick = ${padControls.name}")
        scope.launch {
            try {
                gamePadOutputStream?.apply { writeUTF(Gson().toJson(padControls));flush() }
            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "error for rightGamePad")
                rightGamePadWarning()
            }
        }
    }

    /**
     * used to send mouse pointer values to the desktop
     */
    fun rightJoystick(angle: Int, strength: Int) {
        Log.d(TAG, "rightPad : angle = $angle, strength = $strength")
        scope.launch {
            try {
                mouseTrackOutputStream?.apply {
                    writeUTF(Gson().toJson(MouseData(mouseStrength = strength, mouseAngle = angle)))
                    flush()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "error for rightJoystick")
                rightJoystickWarning()
            }
        }
    }

    fun startStreaming(setImage: (ByteArray) -> Unit) {
        scope.launch {
            while (true) {
                try {
                    // TODO: handle initial failure 
                    val size = screenStream!!.readInt()
                    val jpegImageByteArray = ByteArray(size)
                    screenStream!!.readFully(jpegImageByteArray)
                    Log.d(TAG, "image data received of length = $size")
                    withContext(Dispatchers.Main) { setImage(jpegImageByteArray) }
                } catch (e: Exception) {
                    Log.e(TAG, e.message ?: "error for video")
                    videoWarning()
                }
            }
        }
    }
}
