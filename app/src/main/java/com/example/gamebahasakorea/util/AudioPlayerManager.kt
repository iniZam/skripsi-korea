package com.example.gamebahasakorea.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * Pengelola Audio Pelafalan Bahasa Korea menggunakan Android TextToSpeech (TTS)
 * Mampu melafalkan karakter maupun kata Hangeul secara otomatis dan jernih tanpa memerlukan API eksternal/koneksi internet.
 */
class AudioPlayerManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isTtsReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.KOREA)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("AudioPlayerManager", "Bahasa Korea tidak didukung pada TextToSpeech perangkat ini.")
            } else {
                isTtsReady = true
                tts?.setSpeechRate(0.85f) // Kecepatan agak lambat agar jelas bagi pemula
            }
        } else {
            Log.e("AudioPlayerManager", "Inisialisasi TextToSpeech gagal.")
        }
    }

    /**
     * Memutar pelafalan teks Hangeul (misal: "아", "가", "한")
     */
    fun speakKorean(text: String) {
        if (isTtsReady && text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "HangeulTTS_$text")
        }
    }

    /**
     * Membersihkan resource saat ViewModel/Activity dihancurkan
     */
    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isTtsReady = false
    }
}
