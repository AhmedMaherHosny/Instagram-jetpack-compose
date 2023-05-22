package com.example.instagram.other

import android.content.Context
import android.content.SharedPreferences
import com.example.instagram.other.Constants.SHARED_PREFERENCES_NAME
import com.example.instagram.other.Constants.TOKEN_KEY

class MyPreference(app : Context) {
    private val prefs: SharedPreferences = app.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    fun getToken(): String {
        return prefs.getString(TOKEN_KEY, "")!!
    }
    fun setToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }
}