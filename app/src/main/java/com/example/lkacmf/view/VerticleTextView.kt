package com.example.lkacmf.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.TextView


@SuppressLint("AppCompatCustomView")
class VerticleTextView : TextView {
    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onDraw(canvas: Canvas) {
        val textPaint = paint
        textPaint.color = currentTextColor
        textPaint.drawableState = drawableState
        val bounds = Rect()
        textPaint.getTextBounds("ENTER", 0, "ENTER".length, bounds)
        canvas.save()
        canvas.translate(width / 2F - bounds.height(), (height / 2 + bounds.width() / 1.5F) )
        canvas.rotate(-90f)
        layout.draw(canvas)
        canvas.restore()
    }
}