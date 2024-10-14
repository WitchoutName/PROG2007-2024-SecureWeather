package com.example.secureweather.auth
import android.content.Context
import android.content.SharedPreferences


class AuthService private constructor(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var password: String? = null

    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_PASSWORD = "password"

        // The single instance of AuthService
        @Volatile
        private var INSTANCE: AuthService? = null

        // Lazy initialization of the singleton instance
        fun getInstance(context: Context): AuthService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthService(context).also { INSTANCE = it }
            }
        }
    }

    init {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }


    fun setPassword(password: String){
        // save in local storage
        // password does not need to be encrypted as this is not a serious application
        val editor = preferences.edit()
        editor.putString(KEY_PASSWORD, password)
        editor.apply()
    }

    fun getPassword(): String?{
        // get from local storage
        if (password == null){
            password = preferences.getString(KEY_PASSWORD, null)
        }
        return password
    }
}