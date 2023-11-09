package com.example.lkacmf.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R

class VerLinlayout : LinearLayout {

    private var title //底部标题
            : String? = null
    private var topIcon //左边的图标
            = 0
    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }
    private fun initView(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ButtonLin)
            if (typedArray != null) {
                title = typedArray.getString(R.styleable.ButtonLin_botton_title)
                topIcon = typedArray.getResourceId(
                    R.styleable.ButtonLin_drawer_image_botton,
                    R.drawable.ic_setting
                )
                typedArray.recycle()
            }
        }
        initView(context)
    }

    private fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.button_item, this, true)
        var tvTitle = findViewById<View>(R.id.tvTitle) as TextView
        var icButton = findViewById<View>(R.id.icButton) as ImageView
        tvTitle.text = title
//        tvVersion.text = version
        icButton.setImageResource(topIcon)
    }

}