package com.example.skinfriend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recomendation")
data class RecommendationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val productName: String,
    val price: String,
    val pictureSrc: String,
    val notableEffects: String
)