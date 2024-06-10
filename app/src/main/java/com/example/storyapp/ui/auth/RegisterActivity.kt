package com.example.storyapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.data.ResultState
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.di.Injection
import com.example.storyapp.extensions.showToast
import com.example.storyapp.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory(userRepository = Injection.provideUserRepository(this))
    }
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.registerButton.setOnClickListener { attemptRegister() }
        binding.hyperlinkLogin.setOnClickListener { navigateTo(LoginActivity::class.java) }
    }

    private fun attemptRegister() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        when {
            name.isEmpty() || email.isEmpty() || password.isEmpty() -> showToast(getString(R.string.empty_field_error))
            password.length < 8 -> binding.edRegisterPassword.error = getString(R.string.password_length_error)
            else -> registerViewModel.submitRegister(name, email, password)
        }
    }

    private fun observeViewModel() {
        registerViewModel.responseResult.observe(this) { result ->
            when (result) {
                is ResultState.Success -> handleRegistrationSuccess()
                is ResultState.Error -> showToast(getString(R.string.registration_failed))
                is ResultState.Loading -> binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun handleRegistrationSuccess() {
        showToast(getString(R.string.registration_successful))
        navigateTo(LoginActivity::class.java)
    }

    private fun navigateTo(activityClass: Class<out AppCompatActivity>) {
        startActivity(Intent(this, activityClass))
    }

    @Suppress("DEPRECATION")
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        navigateTo(WelcomeActivity::class.java)
    }
}
