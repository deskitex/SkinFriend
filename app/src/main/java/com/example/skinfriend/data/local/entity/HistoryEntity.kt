package com.example.skinfriend.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "history")
class HistoryEntity (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var skintype: String,
    var oily: String,
    var dry: String,
    var sensitive: String,
    var normal: String,
    var imageUri: String,
    var date: String,
    var isHistory: Boolean
    ): Parcelable

