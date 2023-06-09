package com.example.storyapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class custombtnRegister : AppCompatButton {

    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable
    private var txtColor: Int = 1

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context,attrs,defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground

        setTextColor(ContextCompat.getColor(context, R.color.white))
        textSize = 15f
        gravity = Gravity.CENTER
        text = context.getString(R.string.register)
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context, R.color.white)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.color_button) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.color_button_disable) as Drawable
    }
}