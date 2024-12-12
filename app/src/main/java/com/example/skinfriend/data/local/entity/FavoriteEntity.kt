package com.example.skinfriend.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favorite")
class FavoriteEntity (
    @PrimaryKey val productName: String,
    val price: String,
    val pictureSrc: String,
    val notableEffects1: String,
    val notableEffects2: String,
    val notableEffects3: String,
    val productHref: String,
): Parcelable