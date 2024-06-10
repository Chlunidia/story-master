package com.example.storyapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.databinding.ActivityWelcomeBinding
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.data.local.dataStore
import com.example.storyapp.ui.main.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private val userPreference by lazy { UserPreference.getInstance(dataStore) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = runBlocking { userPreference.getUserToken().first() }
        if (!token.isNullOrEmpty()) {
            navigateToMain()
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        playAnimation()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val logoAlpha = ObjectAnimator.ofFloat(binding.logo, View.ALPHA, 0f, 1f).setDuration(500)
        val titleAlpha = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 0f, 1f).setDuration(500)
        val loginAlpha = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 0f, 1f).setDuration(500)
        val signupAlpha = ObjectAnimator.ofFloat(binding.btnSignUp, View.ALPHA, 0f, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(logoAlpha, titleAlpha, loginAlpha, signupAlpha)
            start()
        }
    }
}
