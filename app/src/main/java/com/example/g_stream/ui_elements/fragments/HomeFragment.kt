package com.example.g_stream.ui_elements.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.g_stream.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private val TAG = HomeFragment::class.java.simpleName
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyBinding()
    }

    private fun applyBinding() {
        binding.apply {
            scanButton.setOnClickListener {
                if (isCameraPermissionGranted()) {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToScanFragment())
                } else {
                    getPermissions()
                }
            }
            documentationButton.setOnClickListener {
                // TODO: open documentation
            }
            desktopIcon.load("https://github.com/Vaishnav-Kanhirathingal/G-Stream-Desktop/raw/main/src/main/resources/app_icon_mipmap/mipmap-xxxhdpi/ic_launcher.png?raw=true")
            desktopGithubButton.setOnClickListener {
                openURL("https://github.com/Vaishnav-Kanhirathingal/G-Stream-Desktop")
            }
            mobileIcon.load("https://github.com/Vaishnav-Kanhirathingal/G-Stream-MOBILE/raw/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png?raw=true")
            androidGithubButton.setOnClickListener {
                openURL("https://github.com/Vaishnav-Kanhirathingal/G-Stream-MOBILE")
            }
        }
    }

    fun openURL(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun getPermissions() {
        Log.d(TAG, "getPermissions called")
        if (isCameraPermissionGranted()) {
            Log.d(TAG, "permission already granted")
        } else {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                1
            )
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        val returnable = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        Log.d(TAG, "returnable = $returnable")
        return returnable
    }
}