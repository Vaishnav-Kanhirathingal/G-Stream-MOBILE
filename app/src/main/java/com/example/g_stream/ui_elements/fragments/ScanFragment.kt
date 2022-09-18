package com.example.g_stream.ui_elements.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.g_stream.connection.ConnectionData
import com.example.g_stream.databinding.FragmentScanBinding
import com.example.g_stream.ui_elements.activity.StreamActivity
import com.example.g_stream.viewmodel.ScanViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class ScanFragment : Fragment() {
    private lateinit var binding: FragmentScanBinding
    private val TAG: String = this::class.java.simpleName

    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null

    private var scanComplete = false

    private val lensFacing = CameraSelector.LENS_FACING_BACK
    private val screenAspectRatio: Int = AspectRatio.RATIO_4_3
    private val cameraSelector: CameraSelector =
        CameraSelector.Builder().requireLensFacing(lensFacing).build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScanBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCamera()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume started")
        scanComplete = false
    }

    private fun setupCamera() {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.requireActivity().application)
        )[ScanViewModel::class.java]
            .processCameraProvider
            .observe(viewLifecycleOwner) {
                cameraProvider = it
                bindCameraUseCases()
            }
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreviewUseCase() {
        if (previewUseCase != null) cameraProvider!!.unbind(previewUseCase)
        previewUseCase = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(binding.cameraPreview.display.rotation)
            .build()
        previewUseCase!!.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
        try {
            cameraProvider!!.bindToLifecycle(this, cameraSelector, previewUseCase)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bindAnalyseUseCase() {
        if (analysisUseCase != null) cameraProvider!!.unbind(analysisUseCase)
        analysisUseCase = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(binding.cameraPreview.display.rotation)
            .build()
        analysisUseCase?.setAnalyzer(Executors.newSingleThreadExecutor()) {
            processImageProxy(BarcodeScanning.getClient(), it)
        }
        try {
            cameraProvider!!.bindToLifecycle(this, cameraSelector, analysisUseCase)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        barcodeScanner
            .process(
                InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
            )
            .addOnSuccessListener { barcodes ->
                barcodes.forEach {
                    try {
                        Log.d(TAG, "value received - ${it.rawValue!!}")
                        Gson().fromJson(it.rawValue!!, ConnectionData::class.java).apply {
                            Log.d(
                                TAG, "value recieved = \n" +
                                        GsonBuilder().setPrettyPrinting().create().toJson(this)
                            )
                        }
                        if (!scanComplete) {
                            scanComplete = true
                            val intent = Intent(this.requireActivity(), StreamActivity::class.java)
                            intent.putExtra(ConnectionData.key, it.rawValue!!)
                            startActivity(intent)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, e.message ?: "error occurred")
                    }
                }
            }
            .addOnFailureListener { it.printStackTrace() }
            .addOnCompleteListener { imageProxy.close() }
    }
}