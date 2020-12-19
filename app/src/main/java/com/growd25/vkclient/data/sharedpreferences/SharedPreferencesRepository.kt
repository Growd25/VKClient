package com.growd25.vkclient.data.sharedpreferences

import android.content.Context
import com.growd25.vkclient.App
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesRepository @Inject constructor(private val app: App) {

    fun tokenIsEmpty(nameToken: String, keyToken: String): Boolean {
        return !app.applicationContext.getSharedPreferences(nameToken, Context.MODE_PRIVATE).contains(keyToken)
    }

    fun getToken(nameToken: String, keyToken: String): String? {
        return (app.applicationContext.getSharedPreferences(nameToken, Context.MODE_PRIVATE).getString(keyToken, ""))
    }

    fun saveToken(nameToken: String, keyToken: String, token: String) {
        app.applicationContext.getSharedPreferences(nameToken, Context.MODE_PRIVATE).edit()
            .putString(keyToken, token)
            .apply()
    }
}
