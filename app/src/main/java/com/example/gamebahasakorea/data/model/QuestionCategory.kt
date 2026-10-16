package com.example.gamebahasakorea.data.model

/**
 * Kategori Materi dan Soal Bahasa Korea (Hangeul)
 */
enum class QuestionCategory {
    VOWEL_BASIC,       // Huruf Vokal Dasar (ㅏ, ㅓ, ㅗ, dll)
    VOWEL_COMPOUND,    // Huruf Vokal Gabungan (ㅐ, ㅒ, ㅔ, ㅖ, ㅘ, dll)
    CONSONANT_BASIC,   // Huruf Konsonan Dasar (ㄱ, ㄴ, ㄷ, ㄹ, ㅁ, dll)
    CONSONANT_DOUBLE,  // Huruf Konsonan Rangkap (ㄲ, ㄸ, ㅃ, ㅆ, ㅉ)
    SYLLABLE_BLOCK,    // Pembentukan Blok Suku Kata (Choseong, Jungseong, Jongseong)
    WORD_TRANSLATION,  // Penerjemah Kata Sederhana Korea-Indo
    MISSING_CHARACTER  // Melengkapi Aksara/Kata (Level 5)
}
