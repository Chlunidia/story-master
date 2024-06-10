package com.example.storyapp.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.utils.CoroutinesTestRule
import com.example.storyapp.utils.DataDummy
import com.example.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    private lateinit var mapsViewModel: MapsViewModel

    private val dummyStories = DataDummy.generateDummyListStory()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mapsViewModel = MapsViewModel(storyRepository)
    }

    @Test
    fun `fetchStoriesWithLocation returns Success`() = runBlockingTest {
        val expectedStories = ResultState.Success(dummyStories)
        val liveData = MutableLiveData<ResultState<List<Story>>>()
        liveData.value = expectedStories

        Mockito.`when`(storyRepository.getStoriesWithLocation()).thenReturn(expectedStories)

        mapsViewModel.fetchStoriesWithLocation()

        val actualStories = mapsViewModel.storiesWithLocation.getOrAwaitValue()

        Mockito.verify(storyRepository).getStoriesWithLocation()
        Assert.assertNotNull(actualStories)
        Assert.assertTrue(actualStories is ResultState.Success)
        Assert.assertEquals(expectedStories, actualStories)
    }

    @Test
    fun `fetchStoriesWithLocation returns Error`() = runBlockingTest {
        val expectedError = ResultState.Error("Error")
        val liveData = MutableLiveData<ResultState<List<Story>>>()
        liveData.value = expectedError

        Mockito.`when`(storyRepository.getStoriesWithLocation()).thenReturn(expectedError)

        mapsViewModel.fetchStoriesWithLocation()

        val actualError = mapsViewModel.storiesWithLocation.getOrAwaitValue()

        Mockito.verify(storyRepository).getStoriesWithLocation()
        Assert.assertNotNull(actualError)
        Assert.assertTrue(actualError is ResultState.Error)
        Assert.assertEquals(expectedError, actualError)
    }
}
