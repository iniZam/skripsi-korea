package com.example.gamebahasakorea.util

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes

class BgmManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentBgmResId: Int? = null

    /**
     * Memutar BGM baru jika berbeda dengan yang sedang berjalan.
     */
    fun playBgm(@RawRes resId: Int, loop: Boolean = true) {
        if (currentBgmResId == resId && mediaPlayer?.isPlaying == true) {
            return // Sudah jalan
        }

        stopBgm()

        try {
            mediaPlayer = MediaPlayer.create(context, resId)?.apply {
                isLooping = loop
                setVolume(0.5f, 0.5f) // Setel volume BGM 50% agar tidak menutupi suara utama/TTS
                start()
            }
            currentBgmResId = resId
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopBgm() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        currentBgmResId = null
    }

    fun pauseBgm() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun resumeBgm() {
        if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
        }
    }

    fun release() {
        stopBgm()
    }
}
