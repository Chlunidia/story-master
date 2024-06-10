package com.example.storyapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.R
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.data.local.dataStore
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.di.Injection
import com.example.storyapp.extensions.showToast
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory(userRepository = Injection.provideUserRepository(this))
    }
    private val userPreference by lazy { UserPreference.getInstance(dataStore) }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener { attemptLogin() }
        binding.hyperlinkRegister.setOnClickListener { navigateTo(RegisterActivity::class.java) }

        observeViewModel()
    }

    private fun attemptLogin() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()

        when {
            email.isEmpty() || password.isEmpty() -> showToast(getString(R.string.empty_field_error))
            password.length < 8 -> binding.edLoginPassword.error =
                getString(R.string.password_length_error)
            else -> loginViewModel.submitLogin(email, password)
        }
    }

    private fun observeViewModel() {
        loginViewModel.responseResult.observe(this) { result ->
            when (result) {
                is ResultState.Success -> handleLoginSuccess(result.data.loginResult?.token)
                is ResultState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast(getString(R.string.login_failed, result.error))
                }
                is ResultState.Loading -> binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun handleLoginSuccess(token: String?) {
        lifecycleScope.launch {
            userPreference.saveUserToken(token ?: "")
            navigateTo(MainActivity::class.java)
        }
    }

    private fun navigateTo(activityClass: Class<out AppCompatActivity>) {
        startActivity(Intent(this, activityClass).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    @Suppress("DEPRECATION")
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        navigateTo(WelcomeActivity::class.java)
    }
}
