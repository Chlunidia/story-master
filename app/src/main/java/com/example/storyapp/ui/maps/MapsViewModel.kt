package com.example.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch
import com.example.storyapp.data.ResultState

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _storiesWithLocation = MutableLiveData<ResultState<List<Story>>>()
    val storiesWithLocation: LiveData<ResultState<List<Story>>> = _storiesWithLocation

    var deferredResult: ResultState<List<Story>>? = null

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            _storiesWithLocation.value = ResultState.Loading
            val result = storyRepository.getStoriesWithLocation()
            _storiesWithLocation.value = result
        }
    }
}
