package com.example.lkacmf.util

import android.bluetooth.le.ScanResult

interface BleWriteCallBack {
    fun writeCallBack(writeBackData:String)
}