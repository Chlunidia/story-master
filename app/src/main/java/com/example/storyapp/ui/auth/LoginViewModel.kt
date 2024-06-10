package com.example.storyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.model.LoginResponse
import com.example.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _responseResult = MutableLiveData<ResultState<LoginResponse>>()
    val responseResult: LiveData<ResultState<LoginResponse>> = _responseResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun submitLogin(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            _responseResult.value = ResultState.Loading
            try {
                userRepository.login(email, password)
                userRepository.loginResult.observeForever { result ->
                    result.onSuccess { loginResponse ->
                        if (!loginResponse.error) {
                            loginResponse.loginResult?.token?.let { token ->
                                viewModelScope.launch {
                                    userRepository.saveUserToken(token)
                                }
                            }
                            _responseResult.value = ResultState.Success(loginResponse)
                        } else {
                            _responseResult.value = ResultState.Error(loginResponse.message)
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
