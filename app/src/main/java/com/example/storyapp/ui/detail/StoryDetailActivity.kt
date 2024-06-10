package com.example.storyapp.ui.detail

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.model.Story
import com.example.storyapp.databinding.ActivityStoryDetailBinding

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val titleTextView = layoutInflater.inflate(R.layout.toolbar_title, null) as TextView
        titleTextView.text = getString(R.string.detail)
        titleTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white))

        val params = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.CENTER
        }

        binding.toolbar.addView(titleTextView, params)

        postponeEnterTransition()

        val story: Story? = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("story", Story::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("story")
        }

        story?.let {
            with(binding) {
                tvName.text = it.name
                tvDescription.text = it.description
                ivPhoto.transitionName = "shared_element_photo"
                Glide.with(this@StoryDetailActivity)
                    .load(it.photoUrl)
                    .into(ivPhoto)
                ivPhoto.viewTreeObserver.addOnPreDrawListener {
                    startPostponedEnterTransition()
                    true
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
