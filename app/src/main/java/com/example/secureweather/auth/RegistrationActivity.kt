package com.example.secureweather.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
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
    private val validator = PasswordValidator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        binding.editTextPassword.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputText = s.toString()
                validator.password = inputText
                updateFeedback()
            }
        })

        binding.editTextPassword.setOnFocusChangeListener { v, hasFocus ->
            binding.editTextPassword.inputType = when(hasFocus) {
                false -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                true -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
        }

        binding.editTextPassword2.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputText = s.toString()
                validator.confirmPassword = inputText
                updateFeedback()
            }
        })


        binding.buttonNext.setOnClickListener {
            validator.update()
            updateFeedback()
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

    fun updateFeedback() {
//        if (!validator.isValid) {
//            binding.editTextPassword.error = validator.feedback
//        } else {
//            binding.editTextPassword.error = null
//        }
        binding.textViewRequirements.text = validator.feedback
        binding.buttonNext.isEnabled = validator.isValid
    }
}