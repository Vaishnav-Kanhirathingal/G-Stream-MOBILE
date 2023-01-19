package com.example.g_stream.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
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
    private var screenStream: DataInputStream? = null

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
                screenStream = DataInputStream(
                    Socket(connectionData.serverIpAddress, connectionData.videoPort)
                        .getInputStream()
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { showConnectionError() }
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startStreaming(setImage: () -> Unit) {
        scope.launch {
            while (true) {
                try {
                    val str = screenStream!!.readUTF()
                    Log.d(TAG, "received = \"${str}\"")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Thread.sleep(100)
                // TODO: convert bytes to image and set it
            }
        }
    }
}
