package com.example.storyapp.extensions

import android.content.Context
import android.widget.Toast

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showNoDataToast() {
    showToast("No data available", Toast.LENGTH_SHORT)
}