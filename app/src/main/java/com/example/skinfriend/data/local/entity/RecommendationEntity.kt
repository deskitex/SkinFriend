package com.example.skinfriend.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recomendation",
    foreignKeys = [
        ForeignKey(
            entity = HistoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["historyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["historyId"])]
)
data class RecommendationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val productName: String,
    val price: String,
    val pictureSrc: String,
    val notableEffects: String,
    val productHref: String,
    val isFavorite:Boolean = false,

    val historyId: Int
)