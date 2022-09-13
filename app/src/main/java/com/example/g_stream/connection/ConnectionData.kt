package com.example.g_stream.connection

// TODO: check and optimize for necessary data
class ConnectionData(
    val serverIpAddress: String,
    val controlPort: Int,
    val streamPort: Int,

    val horizontalResolution: Int,
    val verticalResolution: Int,
    val frameRateCap: Int,
) {
    companion object {
        const val key = "connection_data_key"
    }
}