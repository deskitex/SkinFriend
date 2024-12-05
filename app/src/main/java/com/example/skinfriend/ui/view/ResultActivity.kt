package com.example.skinfriend.ui.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.skinfriend.R
import com.example.skinfriend.databinding.ActivityCameraBinding
import com.example.skinfriend.databinding.ActivityResultBinding
import com.example.skinfriend.ui.model.CameraViewModel
import com.example.skinfriend.ui.model.ViewModelFactory

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    private val cameraViewModel: CameraViewModel by viewModels() {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val oily = intent.getStringExtra(OILY_RESULT)
        val sensitive = intent.getStringExtra(SENSITIVE_RESULT)
        val dry = intent.getStringExtra(DRY_RESULT)
        val normal = intent.getStringExtra(NORMAL_RESULT)

        val formattedText = """
            Oily: $oily
            Sensitive: $sensitive
            Dry: $dry
            Normal: $normal
        """.trimIndent()

        binding.tvResultScan.text = formattedText

    }

    companion object {
        const val OILY_RESULT = "oily"
        const val SENSITIVE_RESULT = "sensitive"
        const val DRY_RESULT = "dry"
        const val NORMAL_RESULT = "normal"
    }
}