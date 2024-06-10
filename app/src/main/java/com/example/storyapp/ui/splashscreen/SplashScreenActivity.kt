package com.example.storyapp.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.data.local.dataStore
import com.example.storyapp.ui.auth.WelcomeActivity
import com.example.storyapp.ui.main.MainActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private val userPreference by lazy { UserPreference.getInstance(dataStore) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        MainScope().launch {
            delay(2000)
            val token = userPreference.getUserToken().firstOrNull()
            if (token.isNullOrEmpty()) {
                navigateTo(WelcomeActivity::class.java)
            } else {
                navigateTo(MainActivity::class.java)
            }
        }
    }

    private fun navigateTo(activityClass: Class<out AppCompatActivity>) {
        startActivity(Intent(this, activityClass))
        finish()
    }
}
