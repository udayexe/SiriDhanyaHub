package com.siridhanya.hub.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences by lazy {
        try {
            EncryptedSharedPreferences.create(
                context,
                "secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback for devices where keystore might be corrupted or unsupported
            context.getSharedPreferences("secure_prefs_fallback", Context.MODE_PRIVATE)
        }
    }

    companion object {
        private const val KEY_PIN = "user_pin"
        private const val KEY_ATTEMPTS = "wrong_attempts"
        private const val KEY_LOCKOUT_TIME = "lockout_time"
        private const val MAX_ATTEMPTS = 5
        private const val LOCKOUT_DURATION = 30 * 1000L // 30 seconds
    }

    fun isPinSet(): Boolean = sharedPreferences.getString(KEY_PIN, null) != null

    fun savePin(pin: String) {
        sharedPreferences.edit().putString(KEY_PIN, pin).apply()
    }

    fun verifyPin(pin: String): Boolean {
        val savedPin = sharedPreferences.getString(KEY_PIN, null)
        return if (savedPin == pin) {
            resetAttempts()
            true
        } else {
            incrementAttempts()
            false
        }
    }

    private fun incrementAttempts() {
        val attempts = sharedPreferences.getInt(KEY_ATTEMPTS, 0) + 1
        sharedPreferences.edit().putInt(KEY_ATTEMPTS, attempts).apply()
        if (attempts >= MAX_ATTEMPTS) {
            sharedPreferences.edit().putLong(KEY_LOCKOUT_TIME, System.currentTimeMillis()).apply()
        }
    }

    private fun resetAttempts() {
        sharedPreferences.edit().putInt(KEY_ATTEMPTS, 0).apply()
        sharedPreferences.edit().putLong(KEY_LOCKOUT_TIME, 0).apply()
    }

    fun isLockedOut(): Boolean {
        val lockoutTime = sharedPreferences.getLong(KEY_LOCKOUT_TIME, 0)
        if (lockoutTime == 0L) return false
        
        val remaining = (lockoutTime + LOCKOUT_DURATION) - System.currentTimeMillis()
        return if (remaining > 0) {
            true
        } else {
            resetAttempts()
            false
        }
    }

    fun getRemainingLockoutTime(): Long {
        val lockoutTime = sharedPreferences.getLong(KEY_LOCKOUT_TIME, 0)
        return maxOf(0, (lockoutTime + LOCKOUT_DURATION) - System.currentTimeMillis())
    }
}
