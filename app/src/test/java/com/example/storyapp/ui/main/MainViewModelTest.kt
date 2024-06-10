package com.example.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.storyapp.utils.CoroutinesTestRule
import com.example.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        mainViewModel = MainViewModel()
    }

    @Test
    fun `when selectFragment is called with discover fragment, selectedFragment and isDiscoverFragment are updated`() = runTest {
        val fragment = Fragment()
        val observerFragment = Observer<Fragment> {}
        val observerIsDiscover = Observer<Boolean> {}

        try {
            mainViewModel.selectedFragment.observeForever(observerFragment)
            mainViewModel.isDiscoverFragment.observeForever(observerIsDiscover)

            mainViewModel.selectFragment(fragment, true)

            val selectedFragment = mainViewModel.selectedFragment.getOrAwaitValue()
            val isDiscoverFragment = mainViewModel.isDiscoverFragment.getOrAwaitValue()

            assertEquals(fragment, selectedFragment)
            assertTrue(isDiscoverFragment)
        } finally {
            mainViewModel.selectedFragment.removeObserver(observerFragment)
            mainViewModel.isDiscoverFragment.removeObserver(observerIsDiscover)
        }
    }

    @Test
    fun `when selectFragment is called with non-discover fragment, selectedFragment and isDiscoverFragment are updated`() = runTest {
        val fragment = Fragment()
        val observerFragment = Observer<Fragment> {}
        val observerIsDiscover = Observer<Boolean> {}

        try {
            mainViewModel.selectedFragment.observeForever(observerFragment)
            mainViewModel.isDiscoverFragment.observeForever(observerIsDiscover)

            mainViewModel.selectFragment(fragment, false)

            val selectedFragment = mainViewModel.selectedFragment.getOrAwaitValue()
            val isDiscoverFragment = mainViewModel.isDiscoverFragment.getOrAwaitValue()

            assertEquals(fragment, selectedFragment)
            assertFalse(isDiscoverFragment)
        } finally {
            mainViewModel.selectedFragment.removeObserver(observerFragment)
            mainViewModel.isDiscoverFragment.removeObserver(observerIsDiscover)
        }
    }
}
