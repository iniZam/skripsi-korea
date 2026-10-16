package com.example.gamebahasakorea.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entitas Database Room untuk Basis Data Soal Aksara Korea
 */
@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val koreanText: String,             // Karakter / Kata Hangeul (contoh: "아", "가", "한")
    val transliteration: String,        // Transliterasi Latin (contoh: "a", "ga", "han")
    val category: String,               // VOWEL_BASIC, VOWEL_COMPOUND, CONSONANT_BASIC, CONSONANT_DOUBLE, SYLLABLE_BLOCK
    val choseong: String? = null,       // Konsonan Awal (Choseong)
    val jungseong: String? = null,      // Vokal Tengah (Jungseong)
    val jongseong: String? = null,      // Konsonan Akhir (Jongseong / Patchim)
    val optionsJson: String,            // Pilihan jawaban dalam bentuk JSON String (misal: ["a", "b", "c", "d"])
    val correctOptionIndex: Int,        // Indeks jawaban benar (0..3)
    val difficultyScore: Double,        // Bobot tingkat kesulitan soal (0.10 sampai 1.00)
    val audioResName: String,           // Nama resource audio pelafalan (misal: "audio_a")
    val imageResName: String? = null    // Nama resource gambar/visual bantuan UI
)
