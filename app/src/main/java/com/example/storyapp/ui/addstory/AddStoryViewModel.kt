package com.example.storyapp.ui.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.model.StoryResponse
import com.example.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _addStoryResult = MutableLiveData<ResultState<StoryResponse>>()
    val addStoryResult: LiveData<ResultState<StoryResponse>> = _addStoryResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun uploadStory(multipartBody: MultipartBody.Part, descriptionBody: RequestBody) {
        viewModelScope.launch {
            _loading.value = true
            val result = storyRepository.uploadStory(multipartBody, descriptionBody)
            _addStoryResult.value = result
            _loading.value = false
        }
    }

    fun uploadStoryWithLocation(
        multipartBody: MultipartBody.Part,
        descriptionBody: RequestBody,
        latBody: RequestBody,
        lonBody: RequestBody
    ) {
        viewModelScope.launch {
            _loading.value = true
            val result = storyRepository.uploadStoryWithLocation(multipartBody, descriptionBody, latBody, lonBody)
            _addStoryResult.value = result
            _loading.value = false
        }
    }
}
