package com.example.skinfriend.preferences.data

data class Question(
    val question: String,
    val options: List<String>,
    val scores: List<Int>
)