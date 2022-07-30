package com.example.g_stream.connection

// TODO: check and optimize for necessary data
class ConnectionData(
    val wifiPort: String,
    val horizontalResolution: Int,
    val verticalResolution: Int,
    val frameRateCap: Int,
    val bluetoothData: String
) {
    companion object {
        const val key = "connection_data_key"
    }
}