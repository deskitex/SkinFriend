package com.example.skinfriend.ui.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.skinfriend.R
import com.example.skinfriend.data.local.entity.FavoriteEntity
import com.example.skinfriend.data.local.entity.HistoryEntity
import com.example.skinfriend.data.local.entity.RecommendationEntity
import com.example.skinfriend.data.remote.response.RecommendationsItem
import com.example.skinfriend.databinding.ActivityResultBinding
import com.example.skinfriend.ui.model.FavoriteViewModel
import com.example.skinfriend.ui.model.HistoryViewModel
import com.example.skinfriend.ui.model.RecomendationViewModel
import com.example.skinfriend.ui.model.ViewModelFactory
import com.example.skinfriend.ui.view.fragment.adapter.RecommendationAdapter
import com.example.skinfriend.util.getDate
import com.example.skinfriend.util.setupRecyclerView
import kotlin.math.round

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    private val recomendationViewModel: RecomendationViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val historyViewModel: HistoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val favoriteViewModel: FavoriteViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var isProcessingFavorite = false


    private lateinit var favoriteEntity: FavoriteEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            scrollView.setParallaxView(previewImage, 0.5f)
        }

        val historyData: HistoryEntity? = intent.getParcelableExtra(EXTRA_HISTORY)

        if (historyData != null) {
            // Jika data dari history, tampilkan data yang sesuai
            displayHistoryData(historyData)
        } else {
            val getUriResult = intent.getStringExtra(IMAGE_RESULT)

            uploadImage(getUriResult)
            getPredictionResult(getUriResult)
        }

        val recommendationAdapter = RecommendationAdapter(object : RecommendationAdapter.OnButtonClickListener{
            override fun onButtonClicked(item: RecommendationsItem) {
                toggleFavorite(item)
            }
        })

        favoriteViewModel.getFavorite().observe(this) { favorites ->
            recomendationViewModel.recommendationResult.observe(this) { recommendations ->
                val updatedRecommendations = recommendations.map { item ->
                    item.copy(
                        isFavorite = favorites.any { it.productName == item.productName }
                    )
                }
                recommendationAdapter.submitList(updatedRecommendations)
            }
        }


        setupRecyclerView(
            binding.rvRecom,
            recommendationAdapter,
            this
        )
    }

    private fun uploadImage(uri: String?) {
        uri?.let { recomendationViewModel.uploadImage(it.toUri(), this) }
        Glide.with(this)
            .load(uri)
            .into(binding.previewImage)
    }

    private fun getPredictionResult(uri: String?) {
        recomendationViewModel.loadingState.observe(this) {
            binding.loadingOverlay.visibility = if (it) View.VISIBLE else View.GONE
        }

        recomendationViewModel.predictionResult.observe(this) { predictions ->
            predictions?.let {
                with(binding) {
                    oilyResult.text = round(it.oily).toString()
                    dryResult.text = round(it.dry).toString()
                    sensitiveResult.text = round(it.sensitive).toString()
                    normalResult.text = round(it.normal).toString()

                    recomendationViewModel.skintypeResult.observe(this@ResultActivity) { skintype ->
                        skintypeResult.text = skintype[0]
                        val historyEntity = HistoryEntity(
                            skintype = skintype[0],
                            oily = round(it.oily).toString(),
                            dry = round(it.dry).toString(),
                            sensitive = round(it.sensitive).toString(),
                            normal = round(it.normal).toString(),
                            imageUri = uri.toString(),
                            date = getDate(),
                            isHistory = true
                        )
                        Log.d("Result", round(it.oily).toString())
                        historyViewModel.insertHistory(listOf(historyEntity))
                    }
                }
            }
        }
        recomendationViewModel.skintypeResult.observe(this) {
            binding.skintypeResult.text = it[0]
        }

        recomendationViewModel.recommendationResult.observe(this) {

        }
    }

    private fun displayHistoryData(history: HistoryEntity) {
        with(binding) {
            // Tampilkan data dari HistoryEntity ke UI
            Glide.with(this@ResultActivity)
                .load(history.imageUri)
                .into(previewImage)
            oilyResult.text = history.oily
            dryResult.text = history.dry
            sensitiveResult.text = history.sensitive
            normalResult.text = history.normal
            skintypeResult.text = history.skintype
        }
    }

    private fun toggleFavorite(item: RecommendationsItem) {
        if (isProcessingFavorite) return

        isProcessingFavorite = true
        val productName = item.productName

        // Check favorite status synchronously and toggle
        favoriteViewModel.isFavoriteSync(productName) { isFavorite ->
            if (isFavorite) {
                removeFromFavorite(item)
            } else {
                saveToFavorite(item)
            }
            isProcessingFavorite = false
        }
    }

    private fun saveToFavorite(item: RecommendationsItem) {
        val notableEffects = item.notableEffects.split(", ")
        val favoriteEntity = FavoriteEntity(
            productName = item.productName,
            pictureSrc = item.pictureSrc,
            notableEffects1 = notableEffects.getOrNull(0) ?: "",
            notableEffects2 = notableEffects.getOrNull(1) ?: "",
            notableEffects3 = notableEffects.getOrNull(2) ?: "",
            price = item.price,
            productHref = item.productHref,
        )
        // Memanggil ViewModel untuk menyimpan data ke database
        favoriteViewModel.insertFavorite(favoriteEntity)
    }

    private fun removeFromFavorite(item: RecommendationsItem) {
        // Memanggil ViewModel untuk menghapus data berdasarkan productName
        favoriteViewModel.deleteFavorite(item.productName)
    }

    companion object {
        const val IMAGE_RESULT = "uri"
        const val EXTRA_HISTORY = "history"
    }
}