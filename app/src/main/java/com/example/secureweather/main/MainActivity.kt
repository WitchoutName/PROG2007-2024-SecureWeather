package com.example.secureweather.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.secureweather.R
import com.example.secureweather.auth.AuthService
import com.example.secureweather.auth.LoginActivity
import com.example.secureweather.databinding.ActivityMainBinding
import com.example.secureweather.auth.RegistrationActivity


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authService = AuthService.getInstance(this)

        if (authService.getPassword() == null){
            // if password is not set, redirect to registration activity
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}