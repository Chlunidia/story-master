package com.example.storyapp.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.extensions.showNoDataToast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DiscoverViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _empty = MutableLiveData<Boolean>()
    val empty: LiveData<Boolean> get() = _empty

    @Suppress("KotlinConstantConditions")
    fun getStories(context: Context): Flow<PagingData<Story>> {
        return repository.getStories()
            .cachedIn(viewModelScope)
            .also { flow ->
                viewModelScope.launch {
                    flow.collect {
                        if (it == emptyList<Story>()) {
                            context.showNoDataToast()
                            _empty.value = true
                        }
                    }
                }
            }
    }

}
