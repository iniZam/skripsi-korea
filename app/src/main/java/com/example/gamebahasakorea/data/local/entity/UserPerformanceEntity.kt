package com.example.gamebahasakorea.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas Database Room untuk Pelacakan Performa Pengguna (Data Mining Log)
 * Merekam 3 variabel utama setiap kali user menjawab soal:
 * 1. Skor (score)
 * 2. Akurasi Jawaban / Benar-Salah (isCorrect)
 * 3. Waktu Pengerjaan dalam Detik (responseTimeSeconds)
 */
@Entity(
    tableName = "user_performance_logs",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["questionId"])]
)
data class UserPerformanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionTimestamp: Long = System.currentTimeMillis(), // Waktu sesi kuis
    val questionId: Int,                                    // ID Soal yang dijawab
    val score: Double,                                       // Skor yang didapat pada soal ini
    val isCorrect: Boolean,                                  // Akurasi: true = Benar, false = Salah
    val responseTimeSeconds: Double,                         // Waktu pengerjaan dalam detik
    val questionDifficultyScore: Double                      // Tingkat kesulitan soal saat dijawab
)
