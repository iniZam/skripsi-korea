package com.example.gamebahasakorea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamebahasakorea.ui.GameViewModel
import com.example.gamebahasakorea.ui.ScreenState
import com.example.gamebahasakorea.ui.learning.LearningModuleScreen
import com.example.gamebahasakorea.ui.learning.SplashScreen
import com.example.gamebahasakorea.ui.main.MainMenuScreen
import com.example.gamebahasakorea.ui.quiz.LevelSelectionScreen
import com.example.gamebahasakorea.ui.quiz.QuizScreen
import com.example.gamebahasakorea.ui.result.PerformanceLogsScreen
import com.example.gamebahasakorea.ui.result.QuizResultScreen
import com.example.gamebahasakorea.ui.theme.GameBahasaKoreaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameBahasaKoreaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppContent()
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: GameViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val questions by viewModel.quizQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedIndex by viewModel.selectedOptionIndex.collectAsState()
    val isSubmitted by viewModel.isAnswerSubmitted.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val summary by viewModel.quizResultSummary.collectAsState()
    val logsHistory by viewModel.performanceLogsHistory.collectAsState()

    when (currentScreen) {
        ScreenState.SPLASH -> {
            SplashScreen()
        }
        ScreenState.MAIN_MENU -> {
            MainMenuScreen(
                onNavigateToLearning = { viewModel.navigateTo(ScreenState.LEARNING_MODULE) },
                onStartQuiz = { viewModel.navigateTo(ScreenState.LEVEL_SELECTION) },
                onNavigateToLogs = { viewModel.navigateTo(ScreenState.PERFORMANCE_LOGS) }
            )
        }
        ScreenState.LEVEL_SELECTION -> {
            LevelSelectionScreen(
                onBack = { viewModel.navigateTo(ScreenState.MAIN_MENU) },
                onSelectLevel = { level -> viewModel.startNewAdaptiveQuiz(level) }
            )
        }
        ScreenState.LEARNING_MODULE -> {
            LearningModuleScreen(
                onBack = { viewModel.navigateTo(ScreenState.MAIN_MENU) },
                onPlayAudio = { text -> viewModel.playAudio(text) }
            )
        }
        ScreenState.QUIZ -> {
            QuizScreen(
                questions = questions,
                currentIndex = currentIndex,
                selectedIndex = selectedIndex,
                isSubmitted = isSubmitted,
                timerSeconds = timerSeconds,
                onSelectOption = { index -> viewModel.selectOption(index) },
                onSubmitAnswer = { viewModel.submitAnswer() },
                onNextQuestion = { viewModel.nextQuestion() },
                onPlayAudio = { text -> viewModel.playAudio(text) }
            )
        }
        ScreenState.QUIZ_RESULT -> {
            QuizResultScreen(
                summary = summary,
                onPlayAgain = { viewModel.startNewAdaptiveQuiz() },
                onBackToMenu = { viewModel.navigateTo(ScreenState.MAIN_MENU) }
            )
        }
        ScreenState.PERFORMANCE_LOGS -> {
            PerformanceLogsScreen(
                logs = logsHistory,
                onBack = { viewModel.navigateTo(ScreenState.MAIN_MENU) }
            )
        }
    }
}
