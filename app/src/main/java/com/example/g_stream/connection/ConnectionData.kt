package com.example.g_stream.connection

class ConnectionData(
    val serverIpAddress: String,

    val videoPort: Int,
    val audioPort: Int,

    val leftGamePadPort: Int,
    val leftJoyStickPort: Int,
    val rightGamePadPort: Int,
    val rightJoyStickPort: Int,
) {
    companion object {
        const val key = "connection_data_key"
    }
}