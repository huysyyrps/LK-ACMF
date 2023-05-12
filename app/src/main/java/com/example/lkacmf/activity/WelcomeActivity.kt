package com.example.lkacmf.activity

import android.os.Bundle
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.adapter.ImageAdapter
import com.example.lkacmf.data.BannerData
import com.example.lkacmf.util.BaseActivity
import com.youth.banner.indicator.CircleIndicator
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : BaseActivity() {
    var i:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val imgList = BannerData.setBannerData()
        banner.apply {
            setIndicator(CircleIndicator(MyApplication.context))
                .setAdapter(ImageAdapter(imgList), true)
        }
        btnIn.setOnClickListener {
            MainActivity.actionStart(this)
            finish()
        }
    }
}
