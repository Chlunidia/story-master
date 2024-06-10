package com.example.storyapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.StoryRemoteMediator
import com.example.storyapp.data.database.StoryDatabase
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.model.StoryResponse
import com.example.storyapp.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

@OptIn(ExperimentalPagingApi::class)
class StoryRepository private constructor(
    private val apiService: ApiService,
    private val database: StoryDatabase
) {

    fun getStories(): Flow<PagingData<Story>> {
        val pagingSourceFactory = { database.storyDao().getAllStories() }

        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = StoryRemoteMediator(database, apiService),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    suspend fun getStoriesWithLocation(): ResultState<List<Story>> {
        return try {
            val response = apiService.getStoriesWithLocation()
            if (!response.error) {
                val stories = response.listStory.map { listStoryItem ->
                    Story(
                        id = listStoryItem.id,
                        name = listStoryItem.name,
                        description = listStoryItem.description,
                        photoUrl = listStoryItem.photoUrl,
                        createdAt = listStoryItem.createdAt,
                        lat = listStoryItem.lat,
                        lon = listStoryItem.lon
                    )
                }
                ResultState.Success(stories)
            } else {
                ResultState.Error(response.message)
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun uploadStory(
        multipartBody: MultipartBody.Part,
        descriptionBody: RequestBody
    ): ResultState<StoryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.addStory(descriptionBody, multipartBody)
                ResultState.Success(response)
            } catch (e: Exception) {
                ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    suspend fun uploadStoryWithLocation(
        multipartBody: MultipartBody.Part,
        descriptionBody: RequestBody,
        latBody: RequestBody,
        lonBody: RequestBody
    ): ResultState<StoryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.addStory(descriptionBody, multipartBody, latBody, lonBody)
                ResultState.Success(response)
            } catch (e: Exception) {
                ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            database: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(
                    apiService,
                    database
                ).also { instance = it }
            }
    }
}
