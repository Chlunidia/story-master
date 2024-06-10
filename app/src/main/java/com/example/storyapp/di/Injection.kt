package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.database.StoryDatabase
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.data.local.dataStore
import com.example.storyapp.data.network.ApiClient
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val dataStore = context.dataStore
        val pref = UserPreference.getInstance(dataStore)
        val token = runBlocking { pref.getUserToken().first() } ?: ""
        val apiService = ApiClient.getApiService(token)
        val database = StoryDatabase.getInstance(context)
        return StoryRepository.getInstance(apiService, database)
    }

    fun provideUserRepository(context: Context): UserRepository {
        val dataStore = context.dataStore
        val pref = UserPreference.getInstance(dataStore)
        val apiService = ApiClient.getApiService("")
        return UserRepository.getInstance(apiService, pref)
    }
}
