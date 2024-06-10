package com.example.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.repository.UserRepository
import com.example.storyapp.ui.addstory.AddStoryViewModel
import com.example.storyapp.ui.auth.LoginViewModel
import com.example.storyapp.ui.auth.RegisterViewModel
import com.example.storyapp.ui.main.DiscoverViewModel
import com.example.storyapp.ui.maps.MapsViewModel

class ViewModelFactory(
    private val userRepository: UserRepository? = null,
    private val storyRepository: StoryRepository? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                userRepository?.let {
                    LoginViewModel(it) as T
                } ?: throw IllegalArgumentException("UserRepository required for LoginViewModel")
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                userRepository?.let {
                    RegisterViewModel(it) as T
                } ?: throw IllegalArgumentException("UserRepository required for RegisterViewModel")
            }
            modelClass.isAssignableFrom(DiscoverViewModel::class.java) -> {
                storyRepository?.let {
                    DiscoverViewModel(it) as T
                } ?: throw IllegalArgumentException("StoryRepository required for StoryViewModel")
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                storyRepository?.let {
                    AddStoryViewModel(it) as T
                } ?: throw IllegalArgumentException("StoryRepository required for AddStoryViewModel")
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                storyRepository?.let {
                    MapsViewModel(it) as T
                } ?: throw IllegalArgumentException("StoryRepository required for MapsViewModel")
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
