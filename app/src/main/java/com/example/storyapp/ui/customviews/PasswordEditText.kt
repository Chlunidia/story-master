package com.example.storyapp.ui.customviews

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class PasswordEditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var passwordButtonImage: Drawable
    private lateinit var passwordIcon: Drawable
    private var isPasswordVisible = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setOnTouchListener(this)
        updatePasswordButtonImage()
        passwordIcon = ContextCompat.getDrawable(context, R.drawable.ic_lock) as Drawable
        setEditCompoundDrawables(endOfTheText = passwordButtonImage, startOfTheText = passwordIcon)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("PasswordEditText", "beforeTextChanged: $s")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                error = if ((s?.length ?: 0) < 8) {
                    resources.getString(R.string.minimum_characters)
                } else {
                    null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("PasswordEditText", "afterTextChanged: $s")
            }
        })
    }

    private fun updatePasswordButtonImage() {
        passwordButtonImage = ContextCompat.getDrawable(
            context,
            if (isPasswordVisible) R.drawable.ic_hide_password else R.drawable.ic_show_password
        ) as Drawable
    }

    private fun setEditCompoundDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val passwordButtonStart: Float
            val passwordButtonEnd: Float
            var isPasswordButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                passwordButtonEnd = (passwordButtonImage.intrinsicWidth + paddingStart).toFloat()
                if (event.x < passwordButtonEnd) isPasswordButtonClicked = true
            } else {
                passwordButtonStart = (width - paddingEnd - passwordButtonImage.intrinsicWidth).toFloat()
                if (event.x > passwordButtonStart) isPasswordButtonClicked = true
            }
            if (isPasswordButtonClicked) {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    isPasswordVisible = !isPasswordVisible
                    updatePasswordButtonImage()
                    setEditCompoundDrawables(
                        endOfTheText = passwordButtonImage,
                        startOfTheText = passwordIcon
                    )
                    transformationMethod =
                        if (isPasswordVisible) null else PasswordTransformationMethod.getInstance()
                    setSelection(text?.length ?: 0) // move cursor to the end
                    return true
                }
            }
        }
        return false
    }
}
