package com.example.secureweather.auth

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.secureweather.R
import com.example.secureweather.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityRegistrationBinding
    private val authService: AuthService = AuthService.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        binding.buttonNext.setOnClickListener {
            if (binding.buttonNext.isEnabled) {
                // save password
                val password = binding.editTextPassword.text.toString()
                authService.setPassword(password)

                // navigate to login activity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}