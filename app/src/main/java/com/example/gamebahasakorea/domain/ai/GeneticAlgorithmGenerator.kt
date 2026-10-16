package com.example.gamebahasakorea.domain.ai

import com.example.gamebahasakorea.domain.model.Chromosome
import com.example.gamebahasakorea.domain.model.Question
import kotlin.math.abs
import kotlin.random.Random

/**
 * Core AI System: Genetic Algorithm Generator
 * Algoritma Genetik untuk memilih dan menyusun kombinasi paket soal kuis secara optimal
 * berdasarkan Target Difficulty yang direkomendasikan oleh Adaptive Learning Engine.
 * 
 * Tahapan Algoritma Genetik:
 * 1. Inisialisasi Populasi Awal
 * 2. Evaluasi Fitness Function
 * 3. Seleksi (Tournament Selection)
 * 4. Crossover (Pindah Silang dengan Perbaikan Duplikat)
 * 5. Mutasi
 * 6. Elitisme
 */
class GeneticAlgorithmGenerator(
    private val populationSize: Int = 30,       // Ukuran populasi kromosom
    private val maxGenerations: Int = 50,       // Jumlah generasi maksimal
    private val crossoverRate: Double = 0.80,   // Probabilitas crossover (80%)
    private val mutationRate: Double = 0.10,    // Probabilitas mutasi (10%)
    private val tournamentSize: Int = 3,        // Ukuran turnamen seleksi
    private val elitismCount: Int = 2           // Jumlah individu terbaik yang langsung diloloskan
) {

    /**
     * Menjalankan Algoritma Genetik untuk menghasilkan paket kuis optimal.
     * 
     * @param questionPool Seluruh bank soal yang tersedia di database
     * @param targetDifficulty Target tingkat kesulitan hasil kalkulasi Adaptive Learning Engine (0.10 - 1.00)
     * @param questionsPerQuiz Jumlah soal dalam satu paket kuis (misal: 5 atau 10 soal)
     * @return List<Question> susunan paket soal kuis terbaik yang telah dioptimasi
     */
    fun generateOptimizedQuiz(
        questionPool: List<Question>,
        targetDifficulty: Double,
        questionsPerQuiz: Int = 5
    ): List<Question> {
        // Validasi ketersediaan bank soal
        if (questionPool.size <= questionsPerQuiz) {
            return questionPool.shuffled()
        }

        // 1. Inisialisasi Populasi Awal
        var population = initializePopulation(questionPool, questionsPerQuiz)

        // Hitung fitness populasi awal
        evaluatePopulationFitness(population, targetDifficulty)

        // Loop Evolusi Generasi GA
        for (generation in 0 until maxGenerations) {
            // Urutkan populasi berdasarkan nilai fitness tertinggi
            population = population.sortedByDescending { it.fitness }.toMutableList()

            // Cek jika solusi terbaik sudah hampir sempurna (fitness >= 0.98)
            if (population.first().fitness >= 0.98) {
                break
            }

            val nextGeneration = mutableListOf<Chromosome>()

            // Elitisme: Simpan kromosom terbaik langsung ke generasi berikutnya
            for (i in 0 until elitismCount) {
                if (i < population.size) {
                    nextGeneration.add(population[i].copy())
                }
            }

            // Pembentukan individu baru sampai ukuran populasi terpenuhi
            while (nextGeneration.size < populationSize) {
                // Seleksi Orang Tua
                val parent1 = tournamentSelection(population)
                val parent2 = tournamentSelection(population)

                // Crossover
                val (child1, child2) = if (Random.nextDouble() < crossoverRate) {
                    crossover(parent1, parent2, questionPool, questionsPerQuiz)
                } else {
                    Pair(parent1.copy(), parent2.copy())
                }

                // Mutasi
                mutate(child1, questionPool)
                mutate(child2, questionPool)

                // Tambahkan anak ke populasi generasi berikutnya
                if (nextGeneration.size < populationSize) nextGeneration.add(child1)
                if (nextGeneration.size < populationSize) nextGeneration.add(child2)
            }

            // Evaluasi fitness generasi baru
            population = nextGeneration
            evaluatePopulationFitness(population, targetDifficulty)
        }

        // Ambil kromosom dengan fitness tertinggi dari generasi terakhir
        val bestChromosome = population.maxByOrNull { it.fitness } ?: population.first()
        return bestChromosome.genes
    }

    /**
     * 1. Pembentukan Populasi Awal (Initial Population)
     * Menggenerasi populasi individu/kromosom acak yang valid
     */
    private fun initializePopulation(
        questionPool: List<Question>,
        quizSize: Int
    ): MutableList<Chromosome> {
        val population = mutableListOf<Chromosome>()
        for (i in 0 until populationSize) {
            // Ambil `quizSize` soal acak tanpa duplikat dari bank soal
            val randomQuestions = questionPool.shuffled().take(quizSize)
            population.add(Chromosome(genes = randomQuestions.toList()))
        }
        return population
    }

    /**
     * 2. Evaluasi Fungsi Fitness (Fitness Function)
     * Menghitung seberapa dekat paket soal dengan target difficulty, varians, dan penalti duplikat.
     * 
     * Formula Fitness:
     * Fitness = 1.0 / (1.0 + (w1 * |AvgDiff - TargetDiff|) + (w2 * Variance) + (w3 * DuplicatePenalty))
     */
    private fun evaluatePopulationFitness(
        population: List<Chromosome>,
        targetDifficulty: Double
    ) {
        val weightDiff = 5.0      // Penalti perbedaan kesulitan dari target
        val weightVariance = 1.5  // Penalti varians kesulitan
        val weightDuplicate = 10.0// Penalti berat jika ada soal duplikat

        for (chromosome in population) {
            val avgDiff = chromosome.getAverageDifficulty()
            val diffError = abs(avgDiff - targetDifficulty)
            val variance = chromosome.getDifficultyVariance()
            val duplicateCount = chromosome.getDuplicateCount()

            val errorPenalty = (weightDiff * diffError) + 
                               (weightVariance * variance) + 
                               (weightDuplicate * duplicateCount)

            // Fitness bernilai antara 0.0 (buruk) sampai 1.0 (sempurna)
            chromosome.fitness = 1.0 / (1.0 + errorPenalty)
        }
    }

    /**
     * 3. Seleksi (Tournament Selection)
     * Memilih kromosom calon orang tua berdasarkan individu terbaik dalam sub-grup acak
     */
    private fun tournamentSelection(population: List<Chromosome>): Chromosome {
        val candidates = mutableListOf<Chromosome>()
        for (i in 0 until tournamentSize) {
            val randomIndex = Random.nextInt(population.size)
            candidates.add(population[randomIndex])
        }
        return candidates.maxByOrNull { it.fitness } ?: candidates.first()
    }

    /**
     * 4. Crossover (Pindah Silang Satu Titik)
     * Menggabungkan gen dari parent1 dan parent2 dengan perbaikan duplikat agar setiap soal unik
     */
    private fun crossover(
        parent1: Chromosome,
        parent2: Chromosome,
        questionPool: List<Question>,
        quizSize: Int
    ): Pair<Chromosome, Chromosome> {
        val crossoverPoint = Random.nextInt(1, quizSize)

        val child1Genes = mutableListOf<Question>()
        val child2Genes = mutableListOf<Question>()

        // Bagian pertama
        child1Genes.addAll(parent1.genes.take(crossoverPoint))
        child2Genes.addAll(parent2.genes.take(crossoverPoint))

        // Bagian kedua
        child1Genes.addAll(parent2.genes.drop(crossoverPoint))
        child2Genes.addAll(parent1.genes.drop(crossoverPoint))

        // Perbaiki duplikat pada child1
        repairDuplicates(child1Genes, questionPool)
        // Perbaiki duplikat pada child2
        repairDuplicates(child2Genes, questionPool)

        return Pair(Chromosome(child1Genes), Chromosome(child2Genes))
    }

    /**
     * Perbaikan Duplikat pada Kromosom Anak setelah proses Crossover
     */
    private fun repairDuplicates(
        genes: MutableList<Question>,
        questionPool: List<Question>
    ) {
        val seenIds = mutableSetOf<Int>()
        for (i in genes.indices) {
            val currentQuestion = genes[i]
            if (seenIds.contains(currentQuestion.id)) {
                // Jika duplikat, ganti dengan soal baru dari pool yang belum digunakan
                val unusedQuestions = questionPool.filter { q -> !seenIds.contains(q.id) && !genes.contains(q) }
                if (unusedQuestions.isNotEmpty()) {
                    val replacement = unusedQuestions.random()
                    genes[i] = replacement
                    seenIds.add(replacement.id)
                }
            } else {
                seenIds.add(currentQuestion.id)
            }
        }
    }

    /**
     * 5. Mutasi (Mutation)
     * Mengganti satu gen (soal) dalam kromosom secara acak dengan soal baru dari question pool
     */
    private fun mutate(chromosome: Chromosome, questionPool: List<Question>) {
        val genes = chromosome.genes.toMutableList()
        var modified = false
        for (i in genes.indices) {
            if (Random.nextDouble() < mutationRate) {
                val currentIds = genes.map { it.id }.toSet()
                val candidateReplacements = questionPool.filter { !currentIds.contains(it.id) }
                if (candidateReplacements.isNotEmpty()) {
                    genes[i] = candidateReplacements.random()
                    modified = true
                }
            }
        }
        if (modified) {
            chromosome.genes = genes
        }
    }
}
