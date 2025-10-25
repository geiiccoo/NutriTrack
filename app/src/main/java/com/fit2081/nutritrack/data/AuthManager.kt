package com.fit2081.nutritrack.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

// Referenced from W8 Lab
object AuthManager {
    private const val PREFS_NAME = "NutriTrackPrefs"
    private const val KEY_USER_ID = "loggedInUserId"

    private var _userId: MutableState<String?> = mutableStateOf(null)
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _userId.value = prefs.getString(KEY_USER_ID, null)
    }

    fun login(context: Context, userId: String) {
        _userId.value = userId
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun logout(context: Context) {
        _userId.value = null
        prefs.edit().remove(KEY_USER_ID)
            .apply()
    }

    fun getUserId(): String? {
        return _userId.value
    }

    fun isUserLoggedIn(): Boolean {
        return !_userId.value.isNullOrEmpty()
    }

    fun setQuestionnaireCompleted(completed: Boolean) {
        val userId = _userId.value ?: return
        prefs.edit().putBoolean("questionnaireCompleted_$userId", completed).apply()
    }

    fun isQuestionnaireCompleted(): Boolean {
        val userId = _userId.value ?: return false
        return prefs.getBoolean("questionnaireCompleted_$userId", false)
    }
}