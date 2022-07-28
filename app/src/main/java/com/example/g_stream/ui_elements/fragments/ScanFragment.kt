package com.example.g_stream.ui_elements.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.g_stream.databinding.FragmentScanBinding
import com.example.g_stream.viewmodel.StreamViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class ScanFragment : Fragment() {
    private val TAG: String = this::class.java.simpleName
    private lateinit var binding: FragmentScanBinding
    private lateinit var cameraSelector: CameraSelector
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null


    private val lensFacing = CameraSelector.LENS_FACING_BACK
    private val screenAspectRatio = AspectRatio.RATIO_4_3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPermissions()
    }

    private fun getPermissions() {
        Log.d(TAG, "getPermissions called")
        if (isCameraPermissionGranted()) {
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
        bindAnalyseUseCase()
    }

    private fun setupCamera() {
        Log.d(TAG, "setupCamera called")
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.activity!!.application)
        )[StreamViewModel::class.java]
            .processCameraProvider
            .observe(this.requireActivity()) {
                // TODO: not being called
                Log.d(TAG, "observer called")
                cameraProvider = it
                if (isCameraPermissionGranted()) bindPreviewUseCase() else getPermissions()
            }
    }

    private fun bindPreviewUseCase() {
        Log.d(TAG, "bindPreviewUseCase called")
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }
        previewUseCase = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(binding.cameraPreview.display.rotation)
            .build()
        previewUseCase!!.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
        try {
            cameraProvider!!.bindToLifecycle(
                this,
                cameraSelector,
                previewUseCase
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bindAnalyseUseCase() {
        Log.d(TAG, "bindAnalyseUseCase called")
        val barcodeScanner = BarcodeScanning.getClient()

        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }

        analysisUseCase = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(binding.cameraPreview.display.rotation)
            .build()
        val cameraExecutor = Executors.newSingleThreadExecutor()
        Log.d(TAG, "setting analysisUseCase analyzer")
        analysisUseCase!!.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { imageProxy ->
            processImageProxy(barcodeScanner, imageProxy)
        })

        try {
            cameraProvider!!.bindToLifecycle(
                this,
                cameraSelector,
                analysisUseCase
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processImageProxy(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy) {
        // TODO: fix error of not being called
        Log.d(TAG, "processImageProxy called")
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                barcodes.forEach {
                    Toast.makeText(requireContext(), "detected image", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "value received = ${it.rawValue!!}")
                }
            }
            .addOnFailureListener {
                Log.e(TAG, it.message!!)
            }.addOnCompleteListener {
                imageProxy.close()
            }
    }
}