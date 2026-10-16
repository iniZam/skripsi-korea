package com.example.gamebahasakorea.domain.model

/**
 * Data Model Log Performa Jawaban Pengguna
 */
data class UserPerformanceData(
    val questionId: Int,
    val score: Double,
    val isCorrect: Boolean,
    val responseTimeSeconds: Double,
    val questionDifficultyScore: Double
)
