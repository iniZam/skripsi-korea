package com.example.gamebahasakorea.ui.learning

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import android.os.Build
import com.example.gamebahasakorea.R

@Composable
fun SplashScreen() {
    var startAnimation by remember { mutableStateOf(false) }
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.7f,
        animationSpec = tween(durationMillis = 1000),
        label = "ScaleAnim"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEBF3FA)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            val context = LocalContext.current
            val imageRequest = ImageRequest.Builder(context)
                .data(R.drawable.splash_anim)
                .decoderFactory(
                    if (Build.VERSION.SDK_INT >= 28) {
                        coil.decode.ImageDecoderDecoder.Factory()
                    } else {
                        coil.decode.GifDecoder.Factory()
                    }
                )
                .build()

            AsyncImage(
                model = imageRequest,
                contentDescription = "Loading Mascot GIF",
                modifier = Modifier
                    .size(220.dp)
                    .scale(scaleAnim)
                    .clip(RoundedCornerShape(24.dp))
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Game Edukasi Aksara Korea",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Belajar Hangeul Interaktif & Adaptif",
                fontSize = 14.sp,
                color = Color(0xFF475569)
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = Color(0xFF2563EB),
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
