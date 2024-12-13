package com.example.skinfriend.ui.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.skinfriend.R
import com.example.skinfriend.data.local.entity.FavoriteEntity
import com.example.skinfriend.data.local.entity.HistoryEntity
import com.example.skinfriend.data.local.entity.RecommendationEntity
import com.example.skinfriend.databinding.ActivityResultBinding
import com.example.skinfriend.ui.model.FavoriteViewModel
import com.example.skinfriend.ui.model.HistoryViewModel
import com.example.skinfriend.ui.model.RecomendationViewModel
import com.example.skinfriend.ui.model.ViewModelFactory
import com.example.skinfriend.ui.view.fragment.adapter.RecommendationAdapter
import com.example.skinfriend.util.getDate
import com.example.skinfriend.util.setupRecyclerView
import kotlinx.coroutines.launch

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
            override fun onButtonClicked(item: RecommendationEntity) {
                toggleFavorite(item)
            }
        })

        favoriteViewModel.getFavorite().observe(this) { favorites ->
            historyViewModel.getHistory().observe(this) { historyList ->
                historyList.lastOrNull()?.let { lastHistory ->
                    historyViewModel.getHistoryWithRecommendationsLive(lastHistory.id).observe(this) { historyWithRecommendations ->
                        val updatedRecommendations = historyWithRecommendations.recommendations.map { item ->
                            item.copy(
                                isFavorite = favorites.any { it.productName == item.productName }
                            )
                        }
                        recommendationAdapter.submitList(updatedRecommendations)
                    }
                }
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
                    oilyResult.text = it.oily.toString()
                    dryResult.text = it.dry.toString()
                    sensitiveResult.text = it.sensitive.toString()
                    normalResult.text = it.normal.toString()

                    recomendationViewModel.skintypeResult.observe(this@ResultActivity) { skintype ->
                        skintypeResult.text = skintype[0]
                        val historyEntity = HistoryEntity(
                            skintype = skintype[0],
                            oily = it.oily.toString(),
                            dry = it.dry.toString(),
                            sensitive = it.sensitive.toString(),
                            normal = it.normal.toString(),
                            imageUri = uri.toString(),
                            date = getDate(),
                            isHistory = true
                        )
                        lifecycleScope.launch {
                            val historyId = historyViewModel.insertHistory(historyEntity)
                            recomendationViewModel.recommendationResult.observe(this@ResultActivity) { recommendation ->
                                val recommendationEntity = recommendation.map { item ->
                                    RecommendationEntity(
                                        productName = item.productName,
                                        price = item.price,
                                        pictureSrc = item.pictureSrc,
                                        notableEffects = item.notableEffects,
                                        productHref = item.productHref,
                                        historyId = historyId.toInt()
                                    )
                                }

                                historyViewModel.insertRecommendation(recommendationEntity)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun displayHistoryData(history: HistoryEntity) {
        with(binding) {
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

    private fun toggleFavorite(item: RecommendationEntity) {
        if (isProcessingFavorite) return

        isProcessingFavorite = true
        val productName = item.productName

        favoriteViewModel.isFavoriteSync(productName) { isFavorite ->
            if (isFavorite) {
                removeFromFavorite(item)
            } else {
                saveToFavorite(item)
            }
            isProcessingFavorite = false
        }
    }

    private fun saveToFavorite(item: RecommendationEntity) {
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
        favoriteViewModel.insertFavorite(favoriteEntity)
    }

    private fun removeFromFavorite(item: RecommendationEntity) {
        favoriteViewModel.deleteFavorite(item.productName)
    }

    companion object {
        const val IMAGE_RESULT = "uri"
        const val EXTRA_HISTORY = "history"
    }
}
