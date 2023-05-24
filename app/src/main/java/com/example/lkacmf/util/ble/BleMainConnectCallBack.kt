package com.example.lkacmf.util.ble

import android.bluetooth.le.ScanResult

interface BleMainConnectCallBack {
    fun onConnectedfinish()
    fun onConnectedfail()
    fun onConnectedsuccess()
    fun onConnectedagain()
}