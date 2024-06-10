package com.example.storyapp.ui.main

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private val _selectedFragment = MutableLiveData<Fragment>()
    val selectedFragment: LiveData<Fragment> get() = _selectedFragment

    private val _isDiscoverFragment = MutableLiveData<Boolean>()
    val isDiscoverFragment: LiveData<Boolean> get() = _isDiscoverFragment

    fun selectFragment(fragment: Fragment, isDiscover: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                _selectedFragment.value = fragment
                _isDiscoverFragment.value = isDiscover
            }
        }
    }
}
