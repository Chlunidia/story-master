package com.example.storyapp.ui.customviews

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import java.util.regex.Pattern

class EmailEditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var emailIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setOnTouchListener(this)
        emailIcon = ContextCompat.getDrawable(context, R.drawable.ic_mail) as Drawable
        setEditCompoundDrawables(startOfTheText = emailIcon)

        addTextChangedListener(object : TextWatcher {
            private var previousText: String = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                previousText = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != previousText) {
                    setTextColor(ContextCompat.getColor(context, R.color.colorValidating))
                }
            }

            override fun afterTextChanged(s: Editable?) {
                validateEmail(s.toString())
            }
        })
    }

    private fun validateEmail(email: String) {
        val emailPattern = Pattern.compile(
            "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        )
        if (!emailPattern.matcher(email).matches()) {
            error = context.getString(R.string.invalid_email)
            setTextColor(ContextCompat.getColor(context, R.color.colorError))
        } else {
            setTextColor(ContextCompat.getColor(context, R.color.colorValid))
        }
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
        if (compoundDrawables[0] != null) {
            val emailIconStart: Float
            val emailIconEnd: Float
            var isEmailIconClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                emailIconEnd = (emailIcon.intrinsicWidth + paddingStart).toFloat()
                if (event.x < emailIconEnd) isEmailIconClicked = true
            } else {
                emailIconStart = (width - paddingEnd - emailIcon.intrinsicWidth).toFloat()
                if (event.x > emailIconStart) isEmailIconClicked = true
            }
            if (isEmailIconClicked) {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    Log.d("EmailEditText", "Email icon clicked")
                    return true
                }
            }
        }
        return false
    }
}
