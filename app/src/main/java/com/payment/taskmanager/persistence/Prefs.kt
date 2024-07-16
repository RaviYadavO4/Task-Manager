package com.payment.taskmanager.persistence

import android.content.SharedPreferences
import androidx.core.content.edit


class Prefs(private val prefs: SharedPreferences) {

    companion object {
        const val PREFS_FILE_NAME = "task_pref"
        private const val PREF_KEY_DARK_MODE = "PREF_KEY_DARK_MODE"

    }



    fun setDarkMode(data: String) {
        prefs.edit { putString(PREF_KEY_DARK_MODE, data) }
    }

    fun getDarkMode(): String =
        prefs.getString(PREF_KEY_DARK_MODE, "") ?: ""





    fun clear() {
        prefs.edit().clear().apply()
    }

}