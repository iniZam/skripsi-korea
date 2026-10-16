package com.example.gamebahasakorea.domain.model

import com.example.gamebahasakorea.data.model.QuestionCategory

/**
 * Domain Model untuk Soal Kuis Aksara Korea
 */
data class Question(
    val id: Int,
    val koreanText: String,
    val transliteration: String,
    val category: QuestionCategory,
    val choseong: String? = null,
    val jungseong: String? = null,
    val jongseong: String? = null,
    val options: List<String>,
    val correctOptionIndex: Int,
    val difficultyScore: Double, // 0.10 - 1.00
    val audioResName: String,
    val imageResName: String? = null
)
