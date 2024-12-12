package com.example.skinfriend.data.remote.response

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class PredictRequest(
    val image: String
)

@Parcelize
data class PredictResponse(

    @field:SerializedName("recommendations")
    val recommendations: List<RecommendationsItem?>? = null,

    @field:SerializedName("predictions")
    val predictions: Predictions? = null,

    @field:SerializedName("skin_types")
    val skinTypes: List<String?>? = null
) : Parcelable

@Parcelize
data class Predictions(

    @field:SerializedName("Oily")
    val oily: Double? = null,

    @field:SerializedName("Sensitive")
    val sensitive: Double?  = null,

    @field:SerializedName("Dry")
    val dry: Double?  = null,

    @field:SerializedName("Normal")
    val normal: Double?  = null
) : Parcelable

@Parcelize
data class RecommendationsItem(

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("notable_effects")
    val notableEffects: String? = null,

    @field:SerializedName("product_href")
    val productHref: String? = null,

    @field:SerializedName("picture_src")
    val pictureSrc: String? = null,

    @field:SerializedName("brand")
    val brand: String? = null,

    @field:SerializedName("product_name")
    val productName: String? = null
) : Parcelable