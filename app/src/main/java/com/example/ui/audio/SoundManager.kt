package com.example.ui.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

/**
 * Manages game audio and feedback sound effects.
 * Uses low-latency ToneGenerator with differentiated pitch/duration envelopes.
 */
class SoundManager(private val context: Context) {
    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    } catch (e: Exception) {
        null
    }
    
    private var isEnabled = true

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    fun playTap() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_PROP_BEEP, 40)
    }

    fun playSelect() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_PROP_ACK, 50)
    }

    fun playLevelSelected() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_KEYPAD_VOLUME_KEY_LITE, 80)
    }

    fun playLevelUnlocked() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 200)
    }

    fun playCorrect() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_PIP, 150)
    }

    fun playWrong() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_LOW_L, 200)
    }

    fun playCodeRun() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_PROP_BEEP2, 80)
    }

    fun playTestPassed() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 160)
    }

    fun playTestFailed() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 220)
    }

    fun playXpEarned() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP, 100)
    }

    fun playStarEarned() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_NETWORK_USA_RINGBACK, 180)
    }

    fun playLevelComplete() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_HIGH_L, 350)
    }

    fun playBossStart() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 300)
    }

    fun playBossDefeated() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_ALERT_AUTOREDIAL_LITE, 400)
    }

    fun playAchievement() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_CONFIRM, 250)
    }

    fun playStreak() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_ALERT_INCALL_LITE, 200)
    }

    fun playReward() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_PRESSHOLDKEY_LITE, 250)
    }

    fun playSuccess() {
        if (!isEnabled) return
        playTone(ToneGenerator.TONE_CDMA_HIGH_L, 300)
    }

    private fun playTone(tone: Int, duration: Int) {
        try {
            toneGenerator?.startTone(tone, duration)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing tone", e)
        }
    }
    
    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            Log.e("SoundManager", "Error releasing tone generator", e)
        }
    }
}
