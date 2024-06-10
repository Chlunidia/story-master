package com.example.storyapp.ui.addstory

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.ResultState
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.di.Injection
import com.example.storyapp.extensions.showToast
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.utils.getImageUri
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import com.example.storyapp.viewmodel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoUri: Uri
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Double? = null
    private var lon: Double? = null

    private val addStoryViewModel: AddStoryViewModel by viewModels {
        ViewModelFactory(storyRepository = Injection.provideStoryRepository(this))
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                showToast(getString(R.string.camera_permission_required))
            }
        }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getCurrentLocation()
            } else {
                showToast(getString(R.string.location_permission_required))
            }
        }

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess: Boolean ->
            if (isSuccess) {
                Glide.with(this).load(currentPhotoUri).into(binding.ivPreview)
            } else {
                showToast(getString(R.string.camera_error_message))
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    currentPhotoUri = uri
                    Glide.with(this).load(currentPhotoUri).into(binding.ivPreview)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupLocationClient()
        setupListeners()
        observeViewModel()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val titleTextView = layoutInflater.inflate(R.layout.toolbar_title, null) as TextView
        titleTextView.text = getString(R.string.upload_button)
        titleTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white))

        val params = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.CENTER
        }

        binding.toolbar.addView(titleTextView, params)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupListeners() {
        binding.btnCamera.setOnClickListener { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
        binding.btnGallery.setOnClickListener { openGallery() }
        binding.btnUpload.setOnClickListener { uploadStory() }

        binding.checkboxAddLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                resetLocation()
            }
        }
    }

    private fun resetLocation() {
        binding.tvCurrentLocation.text = getString(R.string.location_not_available)
        lat = null
        lon = null
    }

    private fun uploadStory() {
        val description = binding.etDescription.text.toString()
        if (description.isEmpty()) {
            binding.etDescription.error = getString(R.string.empty_description_error)
            return
        }

        val file = uriToFile(currentPhotoUri, this).reduceFileImage()
        val multipartBody = MultipartBody.Part.createFormData("photo", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull()))
        val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())

        if (binding.checkboxAddLocation.isChecked && lat != null && lon != null) {
            val latBody = lat.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val lonBody = lon.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            addStoryViewModel.uploadStoryWithLocation(multipartBody, descriptionBody, latBody, lonBody)
        } else {
            addStoryViewModel.uploadStory(multipartBody, descriptionBody)
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let { loc ->
                    lat = loc.latitude
                    lon = loc.longitude
                    binding.tvCurrentLocation.text = getString(R.string.location_coordinates, lat?.let { "%.2f".format(it) }, lon?.let { "%.2f".format(it) })
                } ?: showToast(getString(R.string.unable_to_get_location))
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun observeViewModel() {
        addStoryViewModel.addStoryResult.observe(this) { result ->
            when (result) {
                is ResultState.Success -> {
                    showToast(getString(R.string.story_uploaded_successfully))
                    navigateToMain()
                }
                is ResultState.Error -> {
                    showToast(getString(R.string.upload_failed, result.error))
                }
                is ResultState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun startCamera() {
        currentPhotoUri = getImageUri(this)
        launcherIntentCamera.launch(currentPhotoUri)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }
}
