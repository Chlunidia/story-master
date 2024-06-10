package com.example.storyapp.ui.main

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.adapter.MyStoriesPagingAdapter
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.utils.CoroutinesTestRule
import com.example.storyapp.utils.DataDummy
import com.example.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DiscoverViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var context: Context

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyListStory()
        val pagingData = PagingData.from(dummyStories)
        val flowPagingData = flowOf(pagingData)

        Mockito.`when`(storyRepository.getStories()).thenReturn(flowPagingData)

        val viewModel = DiscoverViewModel(storyRepository)
        val observedData = viewModel.getStories(context).asLiveData().getOrAwaitValue()

        Assert.assertNotNull(observedData)
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyStoriesPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )
        differ.submitData(observedData)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val emptyPagingData = PagingData.empty<Story>()
        val flowPagingData = flowOf(emptyPagingData)

        Mockito.`when`(storyRepository.getStories()).thenReturn(flowPagingData)

        val viewModel = DiscoverViewModel(storyRepository)
        val observedData = viewModel.getStories(context).asLiveData().getOrAwaitValue()

        Assert.assertNotNull(observedData)
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyStoriesPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )
        differ.submitData(observedData)
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
