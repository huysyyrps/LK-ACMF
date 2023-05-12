package com.example.lkacmf.util

import android.bluetooth.le.ScanResult

interface BleReadCallBack {
    fun readCallBack(readData:String)
}