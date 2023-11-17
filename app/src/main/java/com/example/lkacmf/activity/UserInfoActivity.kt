package com.example.lkacmf.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lkacmf.R
import com.example.lkacmf.util.BaseActivity
import kotlinx.android.synthetic.main.activity_user_info.*

class UserInfoActivity : BaseActivity() {
    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, UserInfoActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        webView.loadUrl("file:///android_asset/index.html")
        ivUserInfoBack.setOnClickListener {
            finish()
        }
    }
}