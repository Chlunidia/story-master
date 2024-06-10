package com.example.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.data.model.LoginResponse
import com.example.storyapp.data.model.RegisterResponse
import com.example.storyapp.data.model.ErrorResponse
import com.example.storyapp.data.network.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> = _registerResult

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    suspend fun register(name: String, email: String, password: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(name, email, password)
                Log.d("UserRepository", "User registered successfully")
                _registerResult.postValue(Result.success(response))
            } catch (e: HttpException) {
                val errorResponse = e.response()?.errorBody()?.string()?.let {
                    Gson().fromJson(it, ErrorResponse::class.java)
                }
                Log.e("UserRepository", "HttpException during registration: ${e.message()}")
                _registerResult.postValue(
                    Result.failure(
                        Exception(
                            errorResponse?.message ?: e.message()
                        )
                    )
                )
            } catch (e: Exception) {
                Log.e("UserRepository", "Exception during registration: ${e.message}")
                _registerResult.postValue(Result.failure(e))
            }
        }
    }

    suspend fun login(email: String, password: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(email, password)
                if (response.loginResult?.token != null) {
                    saveUserToken(response.loginResult.token)
                }
                Log.d("UserRepository", "User logged in successfully")
                _loginResult.postValue(Result.success(response))
            } catch (e: HttpException) {
                val errorResponse = e.response()?.errorBody()?.string()?.let {
                    Gson().fromJson(it, ErrorResponse::class.java)
                }
                Log.e("UserRepository", "HttpException during login: ${e.message()}")
                _loginResult.postValue(
                    Result.failure(
                        Exception(
                            errorResponse?.message ?: e.message()
                        )
                    )
                )
            } catch (e: Exception) {
                Log.e("UserRepository", "Exception during login: ${e.message}")
                _loginResult.postValue(Result.failure(e))
            }
        }
    }

    suspend fun saveUserToken(token: String) {
        Log.d("UserRepository", "Saving user token")
        userPreference.saveUserToken(token)
    }

    suspend fun logout() {
        userPreference.clearUserToken()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference).also { instance = it }
            }
    }
}
