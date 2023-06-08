package com.example.lkacmf.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.lk_epk.util.LogUtil

class BatteryReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val current = intent!!.extras!!.getInt("level") // 获得当前电量
        val total = intent!!.extras!!.getInt("scale") // 获得总电量
        val percent = current * 100 / total
        LogUtil.e("TAG","$percent")
    }
}