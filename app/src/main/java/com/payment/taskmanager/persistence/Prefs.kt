package com.payment.taskmanager.persistence

import android.content.SharedPreferences
import androidx.core.content.edit


class Prefs(private val prefs: SharedPreferences) {

    companion object {
        const val PREFS_FILE_NAME = "task_pref"
        private const val PREF_KEY_DARK_MODE1 = "PREF_KEY_DARK_MODE"
        private const val PREF_KEY_DARK_MODE = "PREF_KEY_DARK_MODE"
        private const val PREF_KEY_FIRST_DARK_MODE = "PREF_KEY_FIRST_DARK_MODE"

    }





    fun setDarkMode(data: Boolean) {
        prefs.edit { putBoolean(PREF_KEY_DARK_MODE, data) }
    }

    fun getDarkMode(): Boolean =
        prefs.getBoolean(PREF_KEY_DARK_MODE, false) ?: false

    fun setFirstDarkMode(data: Boolean) {
        prefs.edit { putBoolean(PREF_KEY_FIRST_DARK_MODE, data) }
    }

    fun getFirstDarkMode(): Boolean =
        prefs.getBoolean(PREF_KEY_FIRST_DARK_MODE, false) ?: false





    fun clear() {
        prefs.edit().clear().apply()
    }

}