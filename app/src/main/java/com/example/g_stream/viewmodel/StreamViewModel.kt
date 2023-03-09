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

    private var leftJoystickStream: DataOutputStream? = null
    private var rightJoystickStream: DataOutputStream? = null
    private var leftGamePadStream: DataOutputStream? = null
    private var rightGamePadStream: DataOutputStream? = null
    private var screenStream: DataInputStream? = null
    private var audioStream: DataInputStream? = null

    private val scope = CoroutineScope(Dispatchers.IO)

    private val mouseList = mutableListOf<MouseData>()

    init {
        scope.launch {
            try {
                leftJoystickStream = DataOutputStream(
                    Socket(connectionData.serverIpAddress, connectionData.leftJoyStickPort)
                        .getOutputStream()
                )
                rightJoystickStream = DataOutputStream(
                    Socket(connectionData.serverIpAddress, connectionData.rightJoyStickPort)
                        .getOutputStream()
                )
                leftGamePadStream = DataOutputStream(
                    Socket(connectionData.serverIpAddress, connectionData.leftGamePadPort)
                        .getOutputStream()
                )
                rightGamePadStream = DataOutputStream(
                    Socket(connectionData.serverIpAddress, connectionData.rightGamePadPort)
                        .getOutputStream()
                )
                screenStream = DataInputStream(
                    Socket(connectionData.serverIpAddress, connectionData.videoPort)
                        .getInputStream()
                )
                audioStream = DataInputStream(
                    Socket(connectionData.serverIpAddress, connectionData.audioPort)
                        .getInputStream()
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { showConnectionError() }
                e.printStackTrace()
            }
            initiateRightJoystick()
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
                leftJoystickStream?.apply { writeUTF(Gson().toJson(joyStickControls));flush() }
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
                rightGamePadStream?.apply { writeUTF(Gson().toJson(padControls));flush() }
            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "error for rightGamePad")
                rightGamePadWarning()
            }
        }
    }

    /**
     * used to send mouse pointer values to the desktop
     */
    fun rightJoystick(coordinateX: Int, coordinateY: Int) {
        Log.d(TAG, "rightPad : x = $coordinateX,y = $coordinateY")
        mouseList.add(
            MouseData(
                coordinateX - 50,
                coordinateY - 50
            )
        )
    }

    private suspend fun initiateRightJoystick() {
        try {
            while (true) {
                if (mouseList.isEmpty()) {
                    Thread.sleep(1)
                } else {
                    rightJoystickStream?.apply {
                        val tempList = mutableListOf<String>()
                        for (i in mouseList) {
                            tempList.add(Gson().toJson(i))
                        }
                        mouseList.clear()
                        writeUTF(Gson().toJson(tempList))//-------------------------------takes time
                        flush()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            rightJoystickWarning()
        }
    }

    fun startVideoStreaming(setImage: (ByteArray) -> Unit) {
        scope.launch {
            while (true) {
                try {
                    val size = screenStream!!.readInt()
                    val jpegImageByteArray = ByteArray(size)
                    screenStream!!.readFully(jpegImageByteArray)
                    withContext(Dispatchers.Main) { setImage(jpegImageByteArray) }
                } catch (e: Exception) {
                    Log.e(TAG, e.message ?: "error for video")
                    videoWarning()
                }
            }
        }
    }

    fun startAudioStreaming() {
        scope.launch {
            try {
                while (true) {
                    try {
                        val str = audioStream!!.readUTF()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                audioWarning()
            }
        }
    }
}
