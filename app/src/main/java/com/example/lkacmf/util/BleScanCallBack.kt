package com.example.lkacmf.util

import android.bluetooth.le.ScanResult

interface BleScanCallBack {
    fun scanFinish(scanFinish:String)
    fun scanFail(scanFail:String)
    fun scanItem(scanResult: ScanResult)
}