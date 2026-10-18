package com.example.gamebahasakorea.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebahasakorea.data.local.AppDatabase
import com.example.gamebahasakorea.data.local.entity.UserPerformanceEntity
import com.example.gamebahasakorea.data.repository.GameRepository
import com.example.gamebahasakorea.domain.ai.AdaptiveLearningEngine
import com.example.gamebahasakorea.domain.ai.GeneticAlgorithmGenerator
import com.example.gamebahasakorea.domain.model.Question
import com.example.gamebahasakorea.domain.model.QuizResultSummary
import com.example.gamebahasakorea.domain.model.UserPerformanceData
import com.example.gamebahasakorea.util.AudioPlayerManager
import com.example.gamebahasakorea.util.BgmManager
import com.example.gamebahasakorea.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ScreenState {
    SPLASH,
    MAIN_MENU,
    LEARNING_MODULE,
    LEVEL_SELECTION,
    QUIZ,
    QUIZ_RESULT,
    PERFORMANCE_LOGS
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = GameRepository(
        questionDao = db.questionDao(),
        userPerformanceDao = db.userPerformanceDao(),
        adaptiveEngine = AdaptiveLearningEngine(),
        gaGenerator = GeneticAlgorithmGenerator()
    )

    val audioManager = AudioPlayerManager(application)
    val bgmManager = BgmManager(application)

    // State Navigasi Screen
    private val _currentScreen = MutableStateFlow(ScreenState.SPLASH)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    // State Kuis
    private val _quizQuestions = MutableStateFlow<List<Question>>(emptyList())
    val quizQuestions: StateFlow<List<Question>> = _quizQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow(-1)
    val selectedOptionIndex: StateFlow<Int> = _selectedOptionIndex.asStateFlow()

    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted: StateFlow<Boolean> = _isAnswerSubmitted.asStateFlow()

    // State Timer (Waktu Pengerjaan per Soal)
    private val _timerSeconds = MutableStateFlow(0.0)
    val timerSeconds: StateFlow<Double> = _timerSeconds.asStateFlow()

    private var timerJob: Job? = null

    // State Performa Sesi Kuis Ini
    private val currentSessionLogs = mutableListOf<UserPerformanceData>()

    private val _quizResultSummary = MutableStateFlow<QuizResultSummary?>(null)
    val quizResultSummary: StateFlow<QuizResultSummary?> = _quizResultSummary.asStateFlow()

    // Target Difficulty Terkini
    private val _currentTargetDifficulty = MutableStateFlow(AdaptiveLearningEngine.DEFAULT_INITIAL_DIFFICULTY)
    val currentTargetDifficulty: StateFlow<Double> = _currentTargetDifficulty.asStateFlow()

    // Riwayat Log Performa Keseluruhan
    private val _performanceLogsHistory = MutableStateFlow<List<UserPerformanceEntity>>(emptyList())
    val performanceLogsHistory: StateFlow<List<UserPerformanceEntity>> = _performanceLogsHistory.asStateFlow()

    init {
        viewModelScope.launch {
            repository.populateInitialQuestionsIfNeeded()
            // Splash delay singkat
            delay(1500)
            navigateTo(ScreenState.MAIN_MENU)
        }
    }

    fun navigateTo(screen: ScreenState) {
        if (screen == ScreenState.PERFORMANCE_LOGS) {
            loadPerformanceHistory()
        }
        _currentScreen.value = screen
        
        // Handle BGM berdasarkan Screen State
        when (screen) {
            ScreenState.MAIN_MENU -> bgmManager.playBgm(R.raw.bgm_home)
            ScreenState.LEARNING_MODULE -> bgmManager.playBgm(R.raw.bgm_learning)
            ScreenState.QUIZ -> bgmManager.playBgm(R.raw.bgm_quiz)
            ScreenState.LEVEL_SELECTION, ScreenState.PERFORMANCE_LOGS -> bgmManager.playBgm(R.raw.bgm_home)
            ScreenState.SPLASH -> bgmManager.stopBgm()
            else -> {} // Hasil kuis punya BGM tersendiri
        }
    }

    /**
     * Memulai Sesi Kuis Adaptif Baru dengan Algoritma Genetik berdasarkan Level Terpilih
     */
    fun startNewAdaptiveQuiz(level: Int = -1) {
        viewModelScope.launch {
            val questions = repository.getAdaptiveQuiz(level = level, questionsPerQuiz = 5)
            _quizQuestions.value = questions
            _currentQuestionIndex.value = 0
            _selectedOptionIndex.value = -1
            _isAnswerSubmitted.value = false
            currentSessionLogs.clear()
            _quizResultSummary.value = null

            navigateTo(ScreenState.QUIZ)
            startQuestionTimer()

            // Mainkan audio pelafalan soal pertama secara otomatis jika ada
            questions.firstOrNull()?.let {
                audioManager.speakKorean(it.koreanText)
            }
        }
    }

    /**
     * Memulai Timer Pencatat Waktu Respon Per Soal
     */
    private fun startQuestionTimer() {
        timerJob?.cancel()
        _timerSeconds.value = 0.0
        timerJob = viewModelScope.launch {
            while (true) {
                delay(100) // update per 0.1 detik
                _timerSeconds.value += 0.1
            }
        }
    }

    private fun stopQuestionTimer() {
        timerJob?.cancel()
    }

    fun selectOption(index: Int) {
        if (!_isAnswerSubmitted.value) {
            _selectedOptionIndex.value = index
        }
    }

    /**
     * Mengirimkan Jawaban Soal & Merekam 3 Variabel Data Mining ke Database Lokal (Room)
     */
    fun submitAnswer() {
        if (_isAnswerSubmitted.value || _selectedOptionIndex.value == -1) return

        stopQuestionTimer()
        _isAnswerSubmitted.value = true

        val currentQuestions = _quizQuestions.value
        val question = currentQuestions.getOrNull(_currentQuestionIndex.value) ?: return

        val isCorrect = _selectedOptionIndex.value == question.correctOptionIndex
        val responseTime = Math.round(_timerSeconds.value * 10.0) / 10.0 // pembulatan 1 desimal
        
        // Skor: 100 jika benar, 0 jika salah
        val score = if (isCorrect) 100.0 else 0.0

        val performanceData = UserPerformanceData(
            questionId = question.id,
            score = score,
            isCorrect = isCorrect,
            responseTimeSeconds = responseTime,
            questionDifficultyScore = question.difficultyScore
        )

        currentSessionLogs.add(performanceData)

        // Simpan log ke database Room di background
        viewModelScope.launch {
            repository.recordUserAnswer(performanceData)
        }
    }

    /**
     * Lanjut ke Soal Berikutnya atau Selesaikan Kuis jika sudah selesai
     */
    fun nextQuestion() {
        val nextIndex = _currentQuestionIndex.value + 1
        if (nextIndex < _quizQuestions.value.size) {
            _currentQuestionIndex.value = nextIndex
            _selectedOptionIndex.value = -1
            _isAnswerSubmitted.value = false
            startQuestionTimer()

            // Mainkan audio pelafalan soal berikutnya
            _quizQuestions.value.getOrNull(nextIndex)?.let {
                audioManager.speakKorean(it.koreanText)
            }
        } else {
            // Sesi Kuis Selesai
            finishQuizSession()
        }
    }

    /**
     * Memproses Ringkasan Kuis dengan Adaptive Learning Engine
     */
    private fun finishQuizSession() {
        stopQuestionTimer()
        val adaptiveEngine = AdaptiveLearningEngine()
        val summary = adaptiveEngine.generateResultSummary(
            logs = currentSessionLogs,
            currentTargetDifficulty = _currentTargetDifficulty.value
        )

        _quizResultSummary.value = summary
        _currentTargetDifficulty.value = summary.nextTargetDifficulty
        
        // Mainkan BGM Hasil Kuis berdasarkan nilai (Lebih besar atau sama dengan 50 = Bagus)
        if (summary.accuracyPercentage >= 50.0) {
            bgmManager.playBgm(R.raw.bgm_result_good, loop = false)
        } else {
            bgmManager.playBgm(R.raw.bgm_result_bad, loop = false)
        }
        
        _currentScreen.value = ScreenState.QUIZ_RESULT
    }

    private fun loadPerformanceHistory() {
        viewModelScope.launch {
            db.userPerformanceDao().getAllLogs().collect { logs ->
                _performanceLogsHistory.value = logs
            }
        }
    }

    fun playAudio(koreanText: String) {
        audioManager.speakKorean(koreanText)
    }

    override fun onCleared() {
        super.onCleared()
        audioManager.release()
        bgmManager.release()
    }
}
