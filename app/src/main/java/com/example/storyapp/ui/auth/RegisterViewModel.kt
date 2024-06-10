package com.example.storyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.model.RegisterResponse
import com.example.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _responseResult = MutableLiveData<ResultState<RegisterResponse>>()
    val responseResult: MutableLiveData<ResultState<RegisterResponse>> = _responseResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun submitRegister(name: String, email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                userRepository.register(name, email, password)
                userRepository.registerResult.observeForever { result ->
                    result.onSuccess { registerResponse ->
                        if (!registerResponse.error) {
                            _responseResult.value = ResultState.Success(registerResponse)
                        } else {
                            _responseResult.value = ResultState.Error(registerResponse.message)
                        }
                    }
                    result.onFailure { exception ->
                        _responseResult.value = ResultState.Error(exception.message ?: "Unknown error")
                    }
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _responseResult.value = ResultState.Error(errorBody ?: e.message())
            } finally {
                _loading.value = false
            }
        }
    }
}
