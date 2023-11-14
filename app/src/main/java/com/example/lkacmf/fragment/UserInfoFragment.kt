package com.example.lkacmf.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.example.lkacmf.R

class UserInfoFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()

        var animation = RotateAnimation(
            0F, 90F, // 旋转的起始角度和结束角度（单位：度）
            Animation.RELATIVE_TO_SELF, 0.5f, // 旋转中心点的 X 坐标相对于自身的比例
            Animation.RELATIVE_TO_SELF, 0.5f // 旋转中心点的 Y 坐标相对于自身的比例
        )
    }
}