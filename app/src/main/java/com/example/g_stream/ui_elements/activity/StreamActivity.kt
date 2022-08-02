package com.example.g_stream.ui_elements.activity

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import com.example.g_stream.connection.ConnectionData
import com.example.g_stream.databinding.ActivityStreamBinding

class StreamActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName

    private lateinit var binding: ActivityStreamBinding
    private val hideHandler = Handler(Looper.myLooper()!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val connectionData = intent.getStringExtra(ConnectionData.key)
        Log.d(TAG, "data received = $connectionData!!")

        binding = ActivityStreamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyBinding()
    }

    private fun applyBinding() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.hide()
        hideHandler.removeCallbacks { supportActionBar?.show() }
        hideHandler.postDelayed({
            if (Build.VERSION.SDK_INT >= 30) {
                binding.fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            } else {
                binding.fullscreenContent.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LOW_PROFILE or
                            View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }
        }, 300L)
    }
}