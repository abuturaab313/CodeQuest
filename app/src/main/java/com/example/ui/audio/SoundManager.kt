package com.example.ui.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

/**
 * Manages sound effects for the application.
 * Currently uses ToneGenerator for immediate feedback.
 * Can be extended to use MediaPlayer for custom .mp3 assets in res/raw.
 */
class SoundManager(private val context: Context) {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    
    private var isEnabled = true

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    fun playTap() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_PROP_BEEP, 50)
    }

    fun playCorrect() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_PIP, 150)
    }

    fun playWrong() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_LOW_L, 200)
    }

    fun playSuccess() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_HIGH_L, 300)
    }
    
    fun playSelect() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_PROP_ACK, 50)
    }

    private fun playTone(tone: Int, duration: Int) {
        try {
            toneGenerator.startTone(tone, duration)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing tone", e)
        }
    }
    
    fun release() {
        toneGenerator.release()
    }
}
