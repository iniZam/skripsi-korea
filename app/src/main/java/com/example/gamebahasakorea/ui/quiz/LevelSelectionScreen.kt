package com.example.gamebahasakorea.ui.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Looks3
import androidx.compose.material.icons.filled.Looks4
import androidx.compose.material.icons.filled.Looks5
import androidx.compose.material.icons.filled.LooksOne
import androidx.compose.material.icons.filled.LooksTwo
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gamebahasakorea.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectionScreen(
    onBack: () -> Unit,
    onSelectLevel: (Int) -> Unit
) {
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
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
            title = {
                Text(
                    text = "Pilih Level Kuis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Pilih Tingkat Kesulitan",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Kuis akan menyesuaikan secara adaptif sesuai dengan kemampuanmu di level ini.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(40.dp))

            LevelButton(
                title = "Level 1: Dasar Vokal",
                subtitle = "Pengenalan Huruf Vokal Hangeul",
                icon = Icons.Default.LooksOne,
                backgroundColor = Color(0xFF3B82F6),
                onClick = { onSelectLevel(1) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LevelButton(
                title = "Level 2: Dasar Konsonan",
                subtitle = "Pengenalan Huruf Konsonan Hangeul",
                icon = Icons.Default.LooksTwo,
                backgroundColor = Color(0xFFF59E0B),
                onClick = { onSelectLevel(2) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LevelButton(
                title = "Level 3: Vokal Gabungan & Konsonan Rangkap",
                subtitle = "Gabungan dan Rangkap Hangeul",
                icon = Icons.Default.Looks3,
                backgroundColor = Color(0xFFEF4444),
                onClick = { onSelectLevel(3) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LevelButton(
                title = "Level 4: Kosakata & Terjemahan",
                subtitle = "Blok Suku Kata & Penerjemah Kata",
                icon = Icons.Default.Looks4,
                backgroundColor = Color(0xFF8B5CF6),
                onClick = { onSelectLevel(4) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LevelButton(
                title = "Level 5: Melengkapi Kata",
                subtitle = "Lengkapi aksara yang hilang",
                icon = Icons.Default.Looks5,
                backgroundColor = Color(0xFFEC4899),
                onClick = { onSelectLevel(5) }
            )
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
}

@Composable
fun LevelButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}
