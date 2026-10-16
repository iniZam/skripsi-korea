package com.example.gamebahasakorea.domain.model

/**
 * Model Kromosom untuk Algoritma Genetik
 * Representasi satu individu (susunan kuis yang terdiri dari daftar Soal)
 */
data class Chromosome(
    var genes: List<Question>,      // Gen = Daftar Soal dalam satu paket kuis
    var fitness: Double = 0.0        // Nilai Fitness (kesesuaian dengan target difficulty)
) {
    /**
     * Menghitung Rata-rata Tingkat Kesulitan Paket Soal
     */
    fun getAverageDifficulty(): Double {
        if (genes.isEmpty()) return 0.0
        return genes.map { it.difficultyScore }.average()
    }

    /**
     * Menghitung Varians Kesulitan Soal untuk memastikan variasi soal tidak terlampau membosankan/ekstrem
     */
    fun getDifficultyVariance(): Double {
        if (genes.isEmpty()) return 0.0
        val avg = getAverageDifficulty()
        return genes.sumOf { Math.pow(it.difficultyScore - avg, 2.0) } / genes.size
    }

    /**
     * Mengecek apakah ada soal duplikat dalam paket
     */
    fun getDuplicateCount(): Int {
        val uniqueIds = genes.map { it.id }.toSet()
        return genes.size - uniqueIds.size
    }
}
