package com.example.skinfriend.ui.view

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.skinfriend.databinding.ActivityResultBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil URI gambar dari intent
        val imageUri = intent.getParcelableExtra<Uri>("IMAGE_URI")
        if (imageUri != null) {
            sendImageToApi(imageUri)
        } else {
            Toast.makeText(this, "Gambar tidak ditemukan.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun sendImageToApi(imageUri: Uri) {
        val file = File(imageUri.path ?: "")
        if (!file.exists()) {
            Toast.makeText(this, "File tidak ditemukan.", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            )
            .build()

        val request = Request.Builder()
            .url("https://skincare-recom-api-119416210380.asia-southeast2.run.app/predict")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ResultActivity, "Gagal mengirim data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        handleApiResponse(responseBody)
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@ResultActivity, "Respon kosong dari server.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ResultActivity, "Gagal: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun handleApiResponse(response: String) {
        try {
            val jsonResponse = JSONObject(response)
            val predictions = jsonResponse.getJSONObject("predictions")
            val recommendations = jsonResponse.getJSONArray("recommendations")

            // Tampilkan tipe kulit yang diprediksi
            val skinType = jsonResponse.getJSONArray("skin_types").getString(0)
            runOnUiThread {
                binding.tvSkinType.text = "Tipe Kulit: $skinType"

                // Tampilkan rekomendasi produk (contoh untuk 1 produk)
                val firstRecommendation = recommendations.getJSONObject(0)
                binding.tvRecommendation.text = """
                    Nama Produk: ${firstRecommendation.getString("product_name")}
                    Harga: ${firstRecommendation.getString("price")}
                    Manfaat: ${firstRecommendation.getString("notable_effects")}
                """.trimIndent()
            }
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(this, "Gagal memproses respon: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
