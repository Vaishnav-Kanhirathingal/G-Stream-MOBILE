package com.example.g_stream.ui_elements.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
        binding.scanButton.setOnClickListener {
            if (isCameraPermissionGranted()) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToScanFragment())
            } else {
                getPermissions()
            }
        }
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