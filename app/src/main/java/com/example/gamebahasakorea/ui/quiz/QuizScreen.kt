package com.example.gamebahasakorea.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gamebahasakorea.R
import com.example.gamebahasakorea.domain.model.Question
import com.example.gamebahasakorea.data.model.QuestionCategory
import java.util.Locale

@Composable
fun QuizScreen(
    questions: List<Question>,
    currentIndex: Int,
    selectedIndex: Int,
    isSubmitted: Boolean,
    timerSeconds: Double,
    onSelectOption: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onPlayAudio: (String) -> Unit
) {
    val currentQuestion = questions.getOrNull(currentIndex) ?: return
    val totalQuestions = questions.size
    val progress = (currentIndex + 1).toFloat() / totalQuestions

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_main_menu),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Timer & Progress Counter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Soal ${currentIndex + 1} / $totalQuestions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Timer Badge with jam.jpeg icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFEF3C7))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.jam),
                    contentDescription = "Timer Icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = String.format(Locale.US, "%.1f s", timerSeconds),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB45309)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress Bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF2563EB),
            trackColor = Color(0xFFCBD5E1)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Question Card with Mascot
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Mascot Image in top-left
                Image(
                    painter = painterResource(id = if (currentIndex % 2 == 0) R.drawable.beruang2 else R.drawable.beruang3),
                    contentDescription = "Mascot",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .align(Alignment.TopStart)
                )

                // Audio Button in top-right
                IconButton(
                    onClick = { onPlayAudio(currentQuestion.koreanText) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color(0xFFEFF6FF), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Putar Audio",
                        tint = Color(0xFF2563EB)
                    )
                }

                // Main Hangeul Character Display
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentQuestion.koreanText,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val instructionText = when (currentQuestion.category) {
                        QuestionCategory.MISSING_CHARACTER -> "Lengkapi aksara yang hilang pada kata di atas!"
                        QuestionCategory.MISSING_DOUBLE_CHARACTER -> "Lengkapi 2 aksara yang hilang tanpa petunjuk!"
                        else -> "Manakah transliterasi/bacaan yang benar?"
                    }

                    Text(
                        text = instructionText,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )

                    // Hanya tampilkan petunjuk di Level 5 (MISSING_CHARACTER)
                    if (currentQuestion.category == QuestionCategory.MISSING_CHARACTER) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Petunjuk: ${currentQuestion.transliteration}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF059669),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Options List (4 Choices)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            currentQuestion.options.forEachIndexed { index, option ->
                val isSelected = selectedIndex == index
                val isCorrectAnswer = index == currentQuestion.correctOptionIndex

                val containerColor = when {
                    isSubmitted && isCorrectAnswer -> Color(0xFFDCFCE7) // Hijau jika benar
                    isSubmitted && isSelected -> Color(0xFFFEE2E2)      // Merah jika salah terpilih
                    isSelected -> Color(0xFFDBEAFE)                     // Biru muda jika dipilih
                    else -> Color.White
                }

                val borderColor = when {
                    isSubmitted && isCorrectAnswer -> Color(0xFF16A34A)
                    isSubmitted && isSelected -> Color(0xFFDC2626)
                    isSelected -> Color(0xFF2563EB)
                    else -> Color(0xFFE2E8F0)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                        .clickable(enabled = !isSubmitted) { onSelectOption(index) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${'A' + index}.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = option,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0F172A)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Button: Submit or Next Question
        if (!isSubmitted) {
            Button(
                onClick = onSubmitAnswer,
                enabled = selectedIndex != -1,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    disabledContainerColor = Color(0xFF94A3B8)
                )
            ) {
                Text(
                    text = "Kirim Jawaban",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else {
            Button(
                onClick = onNextQuestion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
            ) {
                Text(
                    text = if (currentIndex + 1 < totalQuestions) "Soal Berikutnya →" else "Lihat Hasil Kuis",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
}
