package com.example.g_stream.ui_elements.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.g_stream.databinding.FragmentScanBinding
import com.example.g_stream.viewmodel.StreamViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class ScanFragment : Fragment() {
    private lateinit var binding: FragmentScanBinding
    private val TAG: String = this::class.java.simpleName

    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null

    private val lensFacing = CameraSelector.LENS_FACING_BACK
    private val screenAspectRatio: Int = AspectRatio.RATIO_4_3

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

    private fun setupCamera() {
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.requireActivity().application)
        )[StreamViewModel::class.java]
            .processCameraProvider
            .observe(viewLifecycleOwner) { provider: ProcessCameraProvider? ->
                cameraProvider = provider
                bindCameraUseCases()
            }
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreviewUseCase() {
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
            cameraProvider!!.bindToLifecycle(this, cameraSelector!!, previewUseCase)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bindAnalyseUseCase() {
        if (cameraProvider == null) return
        if (analysisUseCase != null) cameraProvider!!.unbind(analysisUseCase)
        analysisUseCase = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(binding.cameraPreview.display.rotation)
            .build()
        analysisUseCase?.setAnalyzer(Executors.newSingleThreadExecutor()) {
            processImageProxy(BarcodeScanning.getClient(), it)
        }
        try {
            cameraProvider!!.bindToLifecycle(this, cameraSelector!!, analysisUseCase)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        barcodeScanner
            .process(
                InputImage.fromMediaImage(
                    imageProxy.image!!,
                    imageProxy.imageInfo.rotationDegrees
                )
            )
            .addOnSuccessListener { barcodes ->
                barcodes.forEach {
                    Log.d(TAG, "value received - ${it.rawValue!!}")
                    // TODO: check if the raw value recieved is what we want using a try catch
                }
            }
            .addOnFailureListener { it.printStackTrace() }
            .addOnCompleteListener { imageProxy.close() }
    }
}