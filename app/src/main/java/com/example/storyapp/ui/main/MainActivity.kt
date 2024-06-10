package com.example.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.addstory.AddStoryActivity
import com.example.storyapp.ui.maps.MapsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupToolbarTitle()
        setupBottomNavigation(savedInstanceState)
        observeViewModel()
    }

    private fun setupToolbarTitle() {
        val titleTextView = layoutInflater.inflate(R.layout.toolbar_title, null) as TextView
        titleTextView.text = getString(R.string.app_name)
        titleTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white))

        val params = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.CENTER
        }

        binding.toolbar.addView(titleTextView, params)
    }

    private fun setupBottomNavigation(savedInstanceState: Bundle?) {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_discover -> {
                    viewModel.selectFragment(DiscoverFragment.newInstance(intent.getBooleanExtra("refresh", false)), true)
                    true
                }
                R.id.navigation_settings -> {
                    viewModel.selectFragment(SettingsFragment(), false)
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.navigation_discover
        }
    }

    private fun observeViewModel() {
        viewModel.selectedFragment.observe(this) { fragment ->
            switchFragment(fragment)
        }

        viewModel.isDiscoverFragment.observe(this) { isDiscoverFragment ->
            invalidateOptionsMenu()
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.main_content, fragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val isDiscoverFragment = viewModel.isDiscoverFragment.value ?: false
        menu?.findItem(R.id.action_add_story)?.isVisible = isDiscoverFragment
        menu?.findItem(R.id.action_maps)?.isVisible = isDiscoverFragment
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_story -> {
                val intent = Intent(this, AddStoryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
