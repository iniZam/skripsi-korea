package com.example.gamebahasakorea.ui.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
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
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import android.os.Build
import com.example.gamebahasakorea.R
import com.example.gamebahasakorea.domain.model.QuizResultSummary
import java.util.Locale

@Composable
fun QuizResultScreen(
    summary: QuizResultSummary?,
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit
) {
    if (summary == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Banner Finish Image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.finis),
                contentDescription = "Finish Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mascot & Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val isGoodResult = summary.accuracyPercentage >= 50.0
            val context = LocalContext.current
            val imageRequest = ImageRequest.Builder(context)
                .data(if (isGoodResult) R.drawable.result_good_anim else R.drawable.result_bad_anim)
                .decoderFactory(
                    if (Build.VERSION.SDK_INT >= 28) {
                        ImageDecoderDecoder.Factory()
                    } else {
                        GifDecoder.Factory()
                    }
                )
                .build()

            AsyncImage(
                model = imageRequest,
                contentDescription = "Mascot Result",
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Sesi Kuis Selesai!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
                Text(
                    text = "Berikut ringkasan performa belajarmu",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Score Metrics Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.skor),
                        contentDescription = "Score Icon",
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Total Skor: ${summary.totalScore.toInt()}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MetricItem(
                        label = "Jawaban Benar",
                        value = "${summary.correctAnswers} / ${summary.totalQuestions}"
                    )
                    MetricItem(
                        label = "Akurasi",
                        value = String.format(Locale.US, "%.1f%%", summary.accuracyPercentage)
                    )
                    MetricItem(
                        label = "Rata-Rata Waktu",
                        value = String.format(Locale.US, "%.1f s", summary.averageResponseTimeSeconds)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Adaptive Engine Evaluation & Recommendation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "🧠 Analisis Adaptive Learning Engine",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E40AF)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Target Level Selanjutnya: ${summary.currentDifficultyLevel.description}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D4ED8)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = summary.recommendationMessage,
                    fontSize = 13.sp,
                    color = Color(0xFF334155),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Action Buttons
        Button(
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
        ) {
            Text(
                text = "Lanjut Kuis Adaptif Berikutnya",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackToMenu,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Kembali ke Menu Utama",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )
    }
}
