package com.example.gamebahasakorea.data.model

/**
 * Tingkat Kesulitan Soal
 * Nilai target difficulty direpresentasikan dalam rentang Double 0.1 - 1.0
 */
enum class DifficultyLevel(val minScore: Double, val maxScore: Double, val description: String) {
    LEVEL_1(0.0, 0.25, "Level 1: Pengenalan Huruf Vokal Dasar"),
    LEVEL_2(0.25, 0.50, "Level 2: Pengenalan Huruf Konsonan Dasar"),
    LEVEL_3(0.50, 0.75, "Level 3: Vokal Gabungan & Konsonan Rangkap"),
    LEVEL_4(0.75, 1.00, "Level 4: Blok Suku Kata & Terjemahan Kata Sederhana");

    companion object {
        fun fromValue(value: Double): DifficultyLevel {
            return when {
                value <= 0.25 -> LEVEL_1
                value <= 0.50 -> LEVEL_2
                value <= 0.75 -> LEVEL_3
                else -> LEVEL_4
            }
        }
    }
}
