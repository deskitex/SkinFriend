package com.example.skinfriend.data.remote.response

import com.google.gson.annotations.SerializedName

data class SkincareResponse(

	@field:SerializedName("recommendations")
	val recommendations: List<RecommendationsItem>,

	@field:SerializedName("predictions")
	val predictions: Predictions,

	@field:SerializedName("skin_types")
	val skinTypes: List<String>
)

data class Predictions(

	@field:SerializedName("Oily")
	val oily: Double,

	@field:SerializedName("Sensitive")
	val sensitive: Double,

	@field:SerializedName("Dry")
	val dry: Double,

	@field:SerializedName("Normal")
	val normal: Double
)

data class RecommendationsItem(

	@field:SerializedName("price")
	val price: String,

	@field:SerializedName("notable_effects")
	val notableEffects: String,

	@field:SerializedName("product_href")
	val productHref: String,

	@field:SerializedName("picture_src")
	val pictureSrc: String,

	@field:SerializedName("brand")
	val brand: String,

	@field:SerializedName("product_name")
	val productName: String,

	val isFavorite: Boolean
)
