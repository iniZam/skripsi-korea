package com.example.gamebahasakorea.data.repository

import com.example.gamebahasakorea.data.local.dao.QuestionDao
import com.example.gamebahasakorea.data.local.dao.UserPerformanceDao
import com.example.gamebahasakorea.data.local.entity.QuestionEntity
import com.example.gamebahasakorea.data.local.entity.UserPerformanceEntity
import com.example.gamebahasakorea.data.model.QuestionCategory
import com.example.gamebahasakorea.domain.ai.AdaptiveLearningEngine
import com.example.gamebahasakorea.domain.ai.GeneticAlgorithmGenerator
import com.example.gamebahasakorea.domain.model.Question
import com.example.gamebahasakorea.domain.model.UserPerformanceData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository Pengelola Data Kuis, Performa, dan Engine AI
 */
class GameRepository(
    private val questionDao: QuestionDao,
    private val userPerformanceDao: UserPerformanceDao,
    private val adaptiveEngine: AdaptiveLearningEngine = AdaptiveLearningEngine(),
    private val gaGenerator: GeneticAlgorithmGenerator = GeneticAlgorithmGenerator()
) {

    /**
     * Mengambil paket kuis teradaptasi menggunakan Algoritma Genetik
     */
    suspend fun getAdaptiveQuiz(level: Int = -1, questionsPerQuiz: Int = 5): List<Question> = withContext(Dispatchers.IO) {
        val questionEntities = questionDao.getAllQuestions().filter { entity ->
            when (level) {
                1 -> entity.category == QuestionCategory.VOWEL_BASIC.name
                2 -> entity.category == QuestionCategory.CONSONANT_BASIC.name
                3 -> entity.category == QuestionCategory.VOWEL_COMPOUND.name || entity.category == QuestionCategory.CONSONANT_DOUBLE.name
                4 -> entity.category == QuestionCategory.SYLLABLE_BLOCK.name || entity.category == QuestionCategory.WORD_TRANSLATION.name
                5 -> entity.category == QuestionCategory.MISSING_CHARACTER.name
                else -> true
            }
        }
        val questionPool = questionEntities.map { it.toDomainModel() }

        // Ambil log performa terakhir
        val recentLogs = userPerformanceDao.getRecentLogs(limit = 10).map { it.toDomainModel() }

        // Hitung target difficulty dari Adaptive Learning Engine
        val targetDifficulty = adaptiveEngine.calculateNextTargetDifficulty(recentLogs)

        // Gunakan Genetic Algorithm Generator untuk menghasilkan susunan kuis optimal
        gaGenerator.generateOptimizedQuiz(questionPool, targetDifficulty, questionsPerQuiz)
    }

    /**
     * Merekam jawaban pengguna ke database lokal (Data Mining Log)
     */
    suspend fun recordUserAnswer(performanceData: UserPerformanceData) = withContext(Dispatchers.IO) {
        val entity = UserPerformanceEntity(
            questionId = performanceData.questionId,
            score = performanceData.score,
            isCorrect = performanceData.isCorrect,
            responseTimeSeconds = performanceData.responseTimeSeconds,
            questionDifficultyScore = performanceData.questionDifficultyScore
        )
        userPerformanceDao.insertPerformanceLog(entity)
    }

    /**
     * Pre-populate database awal jika belum ada data soal
     */
    suspend fun populateInitialQuestionsIfNeeded() = withContext(Dispatchers.IO) {
        if (questionDao.getQuestionCount() == 0) {
            val sampleQuestions = listOf(
                QuestionEntity(
                    koreanText = "ㅏ",
                    transliteration = "a",
                    category = QuestionCategory.VOWEL_BASIC.name,
                    optionsJson = "[\"a\", \"i\", \"u\", \"e\"]",
                    correctOptionIndex = 0,
                    difficultyScore = 0.15,
                    audioResName = "vowel_a"
                ),
                QuestionEntity(
                    koreanText = "ㅓ",
                    transliteration = "eo",
                    category = QuestionCategory.VOWEL_BASIC.name,
                    optionsJson = "[\"a\", \"eo\", \"o\", \"u\"]",
                    correctOptionIndex = 1,
                    difficultyScore = 0.20,
                    audioResName = "vowel_eo"
                ),
                QuestionEntity(
                    koreanText = "ㅗ",
                    transliteration = "o",
                    category = QuestionCategory.VOWEL_BASIC.name,
                    optionsJson = "[\"a\", \"i\", \"o\", \"u\"]",
                    correctOptionIndex = 2,
                    difficultyScore = 0.25,
                    audioResName = "vowel_o"
                ),
                QuestionEntity(
                    koreanText = "ㅜ",
                    transliteration = "u",
                    category = QuestionCategory.VOWEL_BASIC.name,
                    optionsJson = "[\"eu\", \"eo\", \"i\", \"u\"]",
                    correctOptionIndex = 3,
                    difficultyScore = 0.25,
                    audioResName = "vowel_u"
                ),
                QuestionEntity(
                    koreanText = "ㅣ",
                    transliteration = "i",
                    category = QuestionCategory.VOWEL_BASIC.name,
                    optionsJson = "[\"i\", \"a\", \"eo\", \"o\"]",
                    correctOptionIndex = 0,
                    difficultyScore = 0.10,
                    audioResName = "vowel_i"
                ),
                QuestionEntity(
                    koreanText = "ㄱ",
                    transliteration = "g/k",
                    category = QuestionCategory.CONSONANT_BASIC.name,
                    optionsJson = "[\"g/k\", \"n\", \"d\", \"r\"]",
                    correctOptionIndex = 0,
                    difficultyScore = 0.15,
                    audioResName = "consonant_g"
                ),
                QuestionEntity(
                    koreanText = "ㅐ",
                    transliteration = "ae",
                    category = QuestionCategory.VOWEL_COMPOUND.name,
                    optionsJson = "[\"ae\", \"yae\", \"e\", \"ye\"]",
                    correctOptionIndex = 0,
                    difficultyScore = 0.45,
                    audioResName = "vowel_ae"
                ),
                QuestionEntity(
                    koreanText = "ㅔ",
                    transliteration = "e",
                    category = QuestionCategory.VOWEL_COMPOUND.name,
                    optionsJson = "[\"ae\", \"e\", \"wa\", \"wi\"]",
                    correctOptionIndex = 1,
                    difficultyScore = 0.50,
                    audioResName = "vowel_e"
                ),
                QuestionEntity(
                    koreanText = "ㅘ",
                    transliteration = "wa",
                    category = QuestionCategory.VOWEL_COMPOUND.name,
                    optionsJson = "[\"wo\", \"we\", \"wa\", \"ui\"]",
                    correctOptionIndex = 2,
                    difficultyScore = 0.60,
                    audioResName = "vowel_wa"
                ),
                QuestionEntity(
                    koreanText = "ㄲ",
                    transliteration = "kk",
                    category = QuestionCategory.CONSONANT_DOUBLE.name,
                    optionsJson = "[\"k\", \"kk\", \"g\", \"n\"]",
                    correctOptionIndex = 1,
                    difficultyScore = 0.55,
                    audioResName = "consonant_kk"
                ),
                QuestionEntity(
                    koreanText = "ㄸ",
                    transliteration = "tt",
                    category = QuestionCategory.CONSONANT_DOUBLE.name,
                    optionsJson = "[\"pp\", \"jj\", \"ss\", \"tt\"]",
                    correctOptionIndex = 3,
                    difficultyScore = 0.65,
                    audioResName = "consonant_tt"
                ),
                QuestionEntity(
                    koreanText = "가",
                    transliteration = "ga",
                    category = QuestionCategory.SYLLABLE_BLOCK.name,
                    choseong = "ㄱ",
                    jungseong = "ㅏ",
                    optionsJson = "[\"ga\", \"na\", \"da\", \"ra\"]",
                    correctOptionIndex = 0,
                    difficultyScore = 0.75,
                    audioResName = "syllable_ga"
                ),
                QuestionEntity(
                    koreanText = "한",
                    transliteration = "han",
                    category = QuestionCategory.SYLLABLE_BLOCK.name,
                    choseong = "ㅎ",
                    jungseong = "ㅏ",
                    jongseong = "ㄴ",
                    optionsJson = "[\"han\", \"kan\", \"san\", \"man\"]",
                    correctOptionIndex = 0,
                    difficultyScore = 0.90,
                    audioResName = "syllable_han"
                ),
                QuestionEntity(
                    koreanText = "안녕하세요",
                    transliteration = "Annyeonghaseyo (Halo)",
                    category = QuestionCategory.WORD_TRANSLATION.name,
                    optionsJson = "[\"Selamat Pagi\", \"Halo\", \"Terima Kasih\", \"Maaf\"]",
                    correctOptionIndex = 1,
                    difficultyScore = 0.95,
                    audioResName = "word_annyeonghaseyo"
                ),
                QuestionEntity(
                    koreanText = "감사합니다",
                    transliteration = "Gamsahamnida (Terima Kasih)",
                    category = QuestionCategory.WORD_TRANSLATION.name,
                    optionsJson = "[\"Permisi\", \"Selamat Tinggal\", \"Terima Kasih\", \"Ya\"]",
                    correctOptionIndex = 2,
                    difficultyScore = 0.98,
                    audioResName = "word_gamsahamnida"
                ),
                QuestionEntity(
                    koreanText = "물",
                    transliteration = "Mul (Air)",
                    category = QuestionCategory.WORD_TRANSLATION.name,
                    optionsJson = "[\"Air\", \"Api\", \"Tanah\", \"Udara\"]",
                    correctOptionIndex = 0,
                    difficultyScore = 0.85,
                    audioResName = "word_mul"
                ),
                QuestionEntity(
                    koreanText = "안...하세요",
                    transliteration = "Annyeonghaseyo (Halo)",
                    category = QuestionCategory.MISSING_CHARACTER.name,
                    optionsJson = "[\"녕\", \"명\", \"년\", \"면\"]",
                    correctOptionIndex = 0,
                    difficultyScore = 0.85,
                    audioResName = "word_annyeonghaseyo"
                ),
                QuestionEntity(
                    koreanText = "감...합니다",
                    transliteration = "Gamsahamnida (Terima Kasih)",
                    category = QuestionCategory.MISSING_CHARACTER.name,
                    optionsJson = "[\"사\", \"자\", \"마\", \"차\"]",
                    correctOptionIndex = 0,
                    difficultyScore = 0.88,
                    audioResName = "word_gamsahamnida"
                ),
                QuestionEntity(
                    koreanText = "한...",
                    transliteration = "Hanguk (Korea)",
                    category = QuestionCategory.MISSING_CHARACTER.name,
                    optionsJson = "[\"만\", \"국\", \"민\", \"멘\"]",
                    correctOptionIndex = 1,
                    difficultyScore = 0.90,
                    audioResName = ""
                ),
                QuestionEntity(
                    koreanText = "사랑...요",
                    transliteration = "Saranghaeyo (Aku cinta kamu)",
                    category = QuestionCategory.MISSING_CHARACTER.name,
                    optionsJson = "[\"해\", \"애\", \"내\", \"매\"]",
                    correctOptionIndex = 0,
                    difficultyScore = 0.92,
                    audioResName = ""
                ),
                QuestionEntity(
                    koreanText = "미안합...다",
                    transliteration = "Mianhamnida (Maaf)",
                    category = QuestionCategory.MISSING_CHARACTER.name,
                    optionsJson = "[\"나\", \"니\", \"네\", \"누\"]",
                    correctOptionIndex = 1,
                    difficultyScore = 0.95,
                    audioResName = ""
                )
            )
            questionDao.insertQuestions(sampleQuestions)
        }
    }

    // Mapper Functions
    private fun QuestionEntity.toDomainModel(): Question {
        // Simple manual JSON array parse
        val optionsList = optionsJson
            .replace("[", "")
            .replace("]", "")
            .replace("\"", "")
            .split(",")
            .map { it.trim() }

        return Question(
            id = id,
            koreanText = koreanText,
            transliteration = transliteration,
            category = QuestionCategory.valueOf(category),
            choseong = choseong,
            jungseong = jungseong,
            jongseong = jongseong,
            options = optionsList,
            correctOptionIndex = correctOptionIndex,
            difficultyScore = difficultyScore,
            audioResName = audioResName,
            imageResName = imageResName
        )
    }

    private fun UserPerformanceEntity.toDomainModel(): UserPerformanceData {
        return UserPerformanceData(
            questionId = questionId,
            score = score,
            isCorrect = isCorrect,
            responseTimeSeconds = responseTimeSeconds,
            questionDifficultyScore = questionDifficultyScore
        )
    }
}
