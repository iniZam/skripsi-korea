package com.example.gamebahasakorea.ui.learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gamebahasakorea.R

data class MaterialItem(
    val hangeul: String,
    val latin: String,
    val description: String,
    val koreaImageRes: Int? = null,
    val indoImageRes: Int? = null,
    val choseong: String? = null,
    val jungseong: String? = null,
    val jongseong: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningModuleScreen(
    onBack: () -> Unit,
    onPlayAudio: (String) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Vokal Dasar", "Konsonan Dasar", "Gabungan & Rangkap", "Blok Suku Kata", "Kata & Terjemahan")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Modul Pembelajaran Hangeul",
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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2563EB))
        )

        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFFEFF6FF),
            contentColor = Color(0xFF2563EB),
            edgePadding = 16.dp
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val currentList = when (selectedTabIndex) {
            0 -> getBasicVowels()
            1 -> getBasicConsonants()
            2 -> getCompoundLetters()
            3 -> getSyllableBlockExamples()
            else -> getWordTranslationExamples()
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(currentList) { item ->
                LearningCard(item = item, onPlayAudio = onPlayAudio)
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun LearningCard(
    item: MaterialItem,
    onPlayAudio: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayAudio(item.hangeul) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display Hangeul Character
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFDBEAFE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.hangeul,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E40AF)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.latin,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = Color(0xFF475569)
                )

                if (item.choseong != null || item.jungseong != null || item.jongseong != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Struktur: [Awal: ${item.choseong ?: "-"}] + [Vokal: ${item.jungseong ?: "-"}] + [Akhir: ${item.jongseong ?: "-"}]",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2563EB)
                    )
                }

                // Show Image assets if available
                if (item.koreaImageRes != null || item.indoImageRes != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item.koreaImageRes?.let { resId ->
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = "Visual Hangeul",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        item.indoImageRes?.let { resId ->
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = "Visual Indonesia",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            IconButton(onClick = { onPlayAudio(item.hangeul) }) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Audio Pelafalan",
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// Data Providers
fun getBasicVowels(): List<MaterialItem> {
    return listOf(
        MaterialItem("ㅏ", "a", "Vokal dasar dibaca 'a' seperti kata 'Anak'", R.drawable.a_korea, R.drawable.a_indo),
        MaterialItem("ㅓ", "eo", "Vokal dasar dibaca 'eo' (o terbuka) seperti 'Orang'", R.drawable.eo_korea, R.drawable.eo_indo),
        MaterialItem("ㅗ", "o", "Vokal dasar dibaca 'o' (o bulat) seperti 'Obat'", R.drawable.o_korea, R.drawable.o_indo),
        MaterialItem("ㅜ", "u", "Vokal dasar dibaca 'u' seperti 'Ular'", R.drawable.u_korea, R.drawable.u_indo),
        MaterialItem("ㅡ", "eu", "Vokal dasar dibaca 'eu' seperti 'Elang'"),
        MaterialItem("ㅣ", "i", "Vokal dasar dibaca 'i' seperti 'Ikan'")
    )
}

fun getBasicConsonants(): List<MaterialItem> {
    return listOf(
        MaterialItem("ㄱ", "g/k", "Konsonan Giyeok dibaca 'g' di awal/tengah, 'k' di akhir", R.drawable.gk_korea, R.drawable.gk_indo),
        MaterialItem("ㄴ", "n", "Konsonan Nieun dibaca 'n' seperti 'Nasi'", R.drawable.n_korea, R.drawable.n_indo),
        MaterialItem("ㄷ", "d/t", "Konsonan Digeut dibaca 'd' atau 't'"),
        MaterialItem("ㄹ", "r/l", "Konsonan Rieul dibaca 'r' atau 'l'"),
        MaterialItem("ㅁ", "m", "Konsonan Mieum dibaca 'm'"),
        MaterialItem("ㅂ", "b/p", "Konsonan Bieup dibaca 'b' atau 'p'"),
        MaterialItem("ㅅ", "s", "Konsonan Siot dibaca 's'"),
        MaterialItem("ㅇ", "ng / -", "Konsonan Ieung (diam di awal, dibaca 'ng' di akhir)"),
        MaterialItem("ㅈ", "j", "Konsonan Jieut dibaca 'j'"),
        MaterialItem("ㅎ", "h", "Konsonan Hieut dibaca 'h'")
    )
}

fun getCompoundLetters(): List<MaterialItem> {
    return listOf(
        MaterialItem("ㅐ", "ae", "Vokal Gabungan (ㅏ + ㅣ) dibaca 'ae'"),
        MaterialItem("ㅔ", "e", "Vokal Gabungan (ㅓ + ㅣ) dibaca 'e'"),
        MaterialItem("ㅘ", "wa", "Vokal Gabungan (ㅗ + ㅏ) dibaca 'wa'"),
        MaterialItem("ㅝ", "weo", "Vokal Gabungan (ㅜ + ㅓ) dibaca 'weo'"),
        MaterialItem("ㄲ", "kk", "Konsonan Rangkap Ssang-Giyeok (Tajam)"),
        MaterialItem("ㄸ", "tt", "Konsonan Rangkap Ssang-Digeut (Tajam)"),
        MaterialItem("ㅃ", "pp", "Konsonan Rangkap Ssang-Bieup (Tajam)"),
        MaterialItem("ㅆ", "ss", "Konsonan Rangkap Ssang-Siot (Tajam)"),
        MaterialItem("ㅉ", "jj", "Konsonan Rangkap Ssang-Jieut (Tajam)")
    )
}

fun getSyllableBlockExamples(): List<MaterialItem> {
    return listOf(
        MaterialItem("가", "ga", "Blok Suku Kata 2 Komponen", choseong = "ㄱ", jungseong = "ㅏ", jongseong = null),
        MaterialItem("나", "na", "Blok Suku Kata 2 Komponen", choseong = "ㄴ", jungseong = "ㅏ", jongseong = null),
        MaterialItem("한", "han", "Blok Suku Kata 3 Komponen (dengan Patchim)", choseong = "ㅎ", jungseong = "ㅏ", jongseong = "ㄴ"),
        MaterialItem("글", "geul", "Blok Suku Kata 3 Komponen (dengan Patchim)", choseong = "ㄱ", jungseong = "ㅡ", jongseong = "ㄹ")
    )
}

fun getWordTranslationExamples(): List<MaterialItem> {
    return listOf(
        MaterialItem("안녕하세요", "Annyeonghaseyo", "Halo / Selamat Pagi"),
        MaterialItem("감사합니다", "Gamsahamnida", "Terima Kasih"),
        MaterialItem("사랑해", "Saranghae", "Aku Cinta Kamu"),
        MaterialItem("네", "Ne", "Ya"),
        MaterialItem("아니요", "Aniyo", "Tidak")
    )
}
