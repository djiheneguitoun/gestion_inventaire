package com.example.myapplication.data
import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "inventaire_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_EXERCICE = "exercice"
        private const val KEY_DOSSIER = "dossier"
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }

    var exercice: String
        get() = prefs.getString(KEY_EXERCICE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_EXERCICE, value).apply()

    var dossier: String
        get() = prefs.getString(KEY_DOSSIER, "") ?: ""
        set(value) = prefs.edit().putString(KEY_DOSSIER, value).apply()

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = prefs.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()
}
