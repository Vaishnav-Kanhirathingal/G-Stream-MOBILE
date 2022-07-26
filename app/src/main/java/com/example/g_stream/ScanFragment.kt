package com.example.g_stream

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.g_stream.databinding.FragmentScanBinding
import com.example.g_stream.ui_elements.StreamActivity

class ScanFragment : Fragment() {
    private lateinit var binding: FragmentScanBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyFragment()
    }

    private fun applyFragment() {
        binding.testButton.setOnClickListener {
            // TODO: go to full screen
            startActivity(Intent(this.requireActivity(), StreamActivity::class.java))
        }
    }
}