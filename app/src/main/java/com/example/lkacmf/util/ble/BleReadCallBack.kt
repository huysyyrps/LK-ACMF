package com.example.lkacmf.util.ble

import android.bluetooth.le.ScanResult

interface BleReadCallBack {
    fun readCallBack(readData:String)
}