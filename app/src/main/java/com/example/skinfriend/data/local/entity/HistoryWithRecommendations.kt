package com.example.skinfriend.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class HistoryWithRecommendations(
    @Embedded val history: HistoryEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "historyId"
    )
    val recommendations: List<RecommendationEntity>
)