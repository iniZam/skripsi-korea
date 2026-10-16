package com.example.gamebahasakorea.domain.ai

import com.example.gamebahasakorea.data.model.DifficultyLevel
import com.example.gamebahasakorea.domain.model.QuizResultSummary
import com.example.gamebahasakorea.domain.model.UserPerformanceData
import kotlin.math.max
import kotlin.math.min

/**
 * Core AI System: Adaptive Learning Engine
 * Menganalisis 3 variabel performa pengguna secara dinamis:
 * 1. Skor (Score)
 * 2. Akurasi Jawaban (Accuracy)
 * 3. Waktu Pengerjaan dalam Detik (Response Time)
 * 
 * Menghasilkan target tingkat kesulitan (targetDifficulty: 0.10 - 1.00)
 * untuk digunakan oleh Genetic Algorithm Generator.
 */
class AdaptiveLearningEngine {

    companion object {
        const val MIN_DIFFICULTY = 0.10
        const val MAX_DIFFICULTY = 1.00
        const val DEFAULT_INITIAL_DIFFICULTY = 0.25 // Mulai dari tingkat dasar
        const val IDEAL_RESPONSE_TIME_SECONDS = 5.0
        const val MAX_THRESHOLD_TIME_SECONDS = 15.0
    }

    /**
     * Menganalisis log performa terbaru dan menghitung Target Difficulty untuk kuis selanjutnya.
     * 
     * @param recentLogs Daftar log jawaban pengguna dari sesi terakhir
     * @param currentTargetDifficulty Target difficulty saat ini (0.10 - 1.00)
     * @return Target difficulty baru yang telah disesuaikan secara adaptif
     */
    fun calculateNextTargetDifficulty(
        recentLogs: List<UserPerformanceData>,
        currentTargetDifficulty: Double = DEFAULT_INITIAL_DIFFICULTY
    ): Double {
        if (recentLogs.isEmpty()) return currentTargetDifficulty

        // 1. Hitung Akurasi (0.0 - 1.0)
        val correctCount = recentLogs.count { it.isCorrect }
        val accuracy = correctCount.toDouble() / recentLogs.size

        // 2. Hitung Rata-Rata Waktu Pengerjaan
        val avgResponseTime = recentLogs.map { it.responseTimeSeconds }.average()

        // Skor efisiensi waktu (1.0 = sangat cepat <= 5s, 0.0 = lambat >= 15s)
        val timeFactor = when {
            avgResponseTime <= IDEAL_RESPONSE_TIME_SECONDS -> 1.0
            avgResponseTime >= MAX_THRESHOLD_TIME_SECONDS -> 0.0
            else -> 1.0 - ((avgResponseTime - IDEAL_RESPONSE_TIME_SECONDS) / (MAX_THRESHOLD_TIME_SECONDS - IDEAL_RESPONSE_TIME_SECONDS))
        }

        // 3. Hitung Rata-Rata Skor Terbobot
        val avgScore = recentLogs.map { it.score }.average()
        val normalizedScore = min(1.0, max(0.0, avgScore / 100.0))

        // Indeks Performa Kombinasi (Performance Index - PI)
        // Bobot: 50% Akurasi, 30% Kecepatan Waktu, 20% Skor
        val performanceIndex = (0.50 * accuracy) + (0.30 * timeFactor) + (0.20 * normalizedScore)

        // Penyesuaian Delta Difficulty
        val learningRate = 0.15
        val deltaDifficulty = when {
            performanceIndex >= 0.80 -> 0.15   // Performa SANGAT TINGGI: tingkatkan kesulitan dengan cepat
            performanceIndex >= 0.65 -> 0.08   // Performa TINGGI: tingkatkan secara moderat
            performanceIndex >= 0.45 -> 0.00   // Performa SEDANG: pertahankan tingkat kesulitan
            performanceIndex >= 0.25 -> -0.08  // Performa RENDAH: turunkan secara moderat
            else -> -0.15                      // Performa SANGAT RENDAH: turunkan ke huruf dasar
        }

        val newTarget = currentTargetDifficulty + (learningRate * deltaDifficulty)
        return min(MAX_DIFFICULTY, max(MIN_DIFFICULTY, newTarget))
    }

    /**
     * Membuat Ringkasan Evaluasi Belajar (QuizResultSummary) beserta rekomendasi umpan balik HCI/UX.
     */
    fun generateResultSummary(
        logs: List<UserPerformanceData>,
        currentTargetDifficulty: Double
    ): QuizResultSummary {
        val total = logs.size
        val correct = logs.count { it.isCorrect }
        val totalScore = logs.sumOf { it.score }
        val accuracy = if (total > 0) (correct.toDouble() / total) * 100.0 else 0.0
        val avgTime = if (total > 0) logs.map { it.responseTimeSeconds }.average() else 0.0

        val nextTarget = calculateNextTargetDifficulty(logs, currentTargetDifficulty)
        val currentLevel = DifficultyLevel.fromValue(currentTargetDifficulty)

        val recommendation = when {
            accuracy >= 80.0 && avgTime <= 6.0 -> 
                "Luar biasa! Pemahaman dan kecepatanmu sangat baik. Soal berikutnya akan ditingkatkan ke tingkat yang lebih menantang."
            accuracy >= 70.0 -> 
                "Bagus sekali! Kamu sudah menguasai materi ini dengan baik. Pertahankan ketelitianmu!"
            accuracy >= 50.0 -> 
                "Cukup baik! Tingkatkan latihan pada bentuk gabungan huruf agar respon milikmu lebih cepat."
            else -> 
                "Tetap semangat! Kami menyesuaikan soal berikutnya ke pengenalan huruf dasar agar kamu bisa memperdalam fondasi dasar Hangeul."
        }

        return QuizResultSummary(
            totalQuestions = total,
            correctAnswers = correct,
            totalScore = totalScore,
            accuracyPercentage = accuracy,
            averageResponseTimeSeconds = avgTime,
            currentDifficultyLevel = currentLevel,
            nextTargetDifficulty = nextTarget,
            recommendationMessage = recommendation
        )
    }
}
