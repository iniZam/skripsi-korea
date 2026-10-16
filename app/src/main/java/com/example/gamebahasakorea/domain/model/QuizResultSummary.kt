package com.example.gamebahasakorea.domain.model

import com.example.gamebahasakorea.data.model.DifficultyLevel

/**
 * Ringkasan Evaluasi Belajar Setelah Kuis Selesai
 */
data class QuizResultSummary(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val totalScore: Double,
    val accuracyPercentage: Double,          // Akurasi (%)
    val averageResponseTimeSeconds: Double,  // Rata-rata waktu pengerjaan per soal (detik)
    val currentDifficultyLevel: DifficultyLevel,
    val nextTargetDifficulty: Double,        // Difficulty target untuk sesi berikutnya
    val recommendationMessage: String        // Pesan evaluasi/umpan balik adaptif
)
