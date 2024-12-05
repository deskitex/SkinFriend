package com.example.skinfriend.ui.view

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.skinfriend.databinding.ActivityCameraBinding
import com.example.skinfriend.ui.model.CameraViewModel
import com.example.skinfriend.ui.model.ViewModelFactory
import com.example.skinfriend.util.*
import com.yalantis.ucrop.UCrop
import java.io.File

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null

    private var currentImageUri: Uri? = null

    private val cameraViewModel: CameraViewModel by viewModels() {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.switchCamera.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }
        //Menekan tombol capture akan menjalankan fungsi takePhoto
        binding.captureImage.setOnClickListener { takePhoto() }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createCustomTempFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    val getUri = output.savedUri
                    setResult(CAMERAX_RESULT, intent)
                    //Setelah mendapatkan uri photo menjalankan fungsi start crop
                    getUri?.let { startCrop(it) }
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "onError: ${exc.message}")
                }
            }
        )
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }
        }
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    private fun startCrop(uri: Uri) {
        Log.d(TAG, "Starting crop with URI: $uri")

        val fileName = "croppedImage_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(this.cacheDir, fileName))
        val options = UCrop.Options()
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        options.setCompressionQuality(100)
        val uCrop = UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1080, 1080)
            .withOptions(options)
        val cropIntent = uCrop.getIntent(this)
        cropResultLauncher.launch(cropIntent)

        Log.d("currentImage", "$currentImageUri")
    }

    private val cropResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "startCamera")

        if (result.resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let {
                //Setelah berhasil crop jalankan fungsi upload image dari uri hasil crop
                cameraViewModel.uploadImage(it, this)
                cameraViewModel.predictionResult.observe(this){ predictions->
                    predictions?.let {
                        val intent = Intent(this, ResultActivity::class.java).apply {
                            putExtra(ResultActivity.OILY_RESULT, "${it.oily}")
                            putExtra(ResultActivity.SENSITIVE_RESULT, "${it.sensitive}")
                            putExtra(ResultActivity.DRY_RESULT, "${it.dry}")
                            putExtra(ResultActivity.NORMAL_RESULT, "${it.normal}")
                        }
                        startActivity(intent)
                    }
                }
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            Log.d(TAG, "Crop Error: ")

            val cropError = UCrop.getError(result.data!!)
            cropError?.let {
                MainActivity.showToast(this, "Crop error: ${it.message}")
            }
        }
    }

    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 200
    }
}