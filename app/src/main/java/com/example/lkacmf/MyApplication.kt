package com.example.lkacmf

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.sscl.bluetoothlowenergylibrary.BleManager
import com.sscl.bluetoothlowenergylibrary.Logger
import com.sscl.bluetoothlowenergylibrary.scanner.BleScanner
import update.UpdateAppUtils

class MyApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        //软件更新库
        UpdateAppUtils.init(context)
        //初始化BLE库
        BleManager.initialize(this)
        //开启BLE库日志打印
        Logger.enableLog(true)
    }
}