package com.example.birdapp.utils

import android.content.Context

object SessionUtils {
    private const val PREF_NAME = "birdapp_prefs"
    private const val KEY_FIRST_LAUNCH = "isFirstLaunch"

    fun isFirstLaunch(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchDone(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }
}
