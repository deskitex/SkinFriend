package com.example.skinfriend.ui.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.skinfriend.databinding.ActivityPredictBinding
import com.example.skinfriend.data.remote.retrofit.ApiConfig
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PredictActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPredictBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra("image_uri") ?: return
        Log.d("PredictActivity", "Received image_uri: $imageUri")

        val file = getFileFromUri(Uri.parse(imageUri))
        if (file == null || !file.exists() || !file.canRead()) {
            Toast.makeText(this, "File tidak ditemukan atau tidak dapat dibaca", Toast.LENGTH_SHORT).show()
            Log.e("PredictActivity", "File not found or unreadable: ${file?.absolutePath}")
            return
        }

        val imageView: ImageView = binding.imagePreview
        imageView.setImageURI(Uri.parse(imageUri))

        uploadImage(file)
    }

    private fun uploadImage(file: File) {
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
        ApiConfig.getPredicttService().predictImage(part).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                runOnUiThread {
                    Toast.makeText(this@PredictActivity, "Gagal mengunggah gambar: ${t.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("PredictActivity", "onFailure: ${t.message}", t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    Log.d("PredictActivity", "Response: $responseBody")
                    runOnUiThread {
                        binding.textViewResponse.text = responseBody ?: "No response data"
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@PredictActivity, "Gagal menerima respons: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)
            tempFile.outputStream().use { inputStream?.copyTo(it) }
            tempFile
        } catch (e: Exception) {
            Log.e("PredictActivity", "Error getting file from URI", e)
            null
        }
    }
}
