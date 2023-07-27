package com.example.lkacmf.util.usb

import android.app.PendingIntent
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.util.Constant
import com.example.lkacmf.util.HexUtil
import com.example.lkacmf.util.ble.BleDataMake
import com.example.lkacmf.util.showToast
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import com.sscl.baselibrary.utils.getByteArray
import okhttp3.internal.and
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object UsbContent {
    var portOne: UsbSerialPort? = null
    var portMore: UsbSerialPort? = null
    var connectState:Boolean  = false
    @RequiresApi(Build.VERSION_CODES.O)
    fun usbDeviceConstant(content: MainActivity, usbBackDataLisition: UsbBackDataLisition):Boolean{
        val manager = content.getSystemService(AppCompatActivity.USB_SERVICE) as UsbManager
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            content.resources.getString(R.string.no_devices).showToast(content)
            connectState = false
            return connectState
        }
       for (device in availableDrivers){
           LogUtil.e("TAG","${device.device.productId}")
           if (device.device.productId==Constant.PID){
               val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
               val permissionIntent = PendingIntent.getBroadcast(content, 0,
                   Intent("com.android.example.USB_PERMISSION"), flags
               )
               manager.requestPermission(device.device, permissionIntent)
               val connectionOne = manager.openDevice(device.device)
               if (connectionOne==null){
                   connectState = false
                   return connectState
               }
               portOne = device.ports[0]
               portOne?.open(connectionOne)
               //设置串口的波特率、数据位，停止位，校验位115200
               portOne?.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
               val mExecutorOne: ExecutorService = Executors.newSingleThreadExecutor()
               val mSerialIoManagerOne: SerialInputOutputManager
               val mListenerOne: SerialInputOutputManager.Listener = object : SerialInputOutputManager.Listener {
                   override fun onRunError(e: Exception) {
                       LogUtil.e("TAG", e.toString())
                   }
                   override fun onNewData(data: ByteArray) {
                       //TODO 新的数据
                       byteArrayToHexStr(data)?.let {
                           usbBackDataLisition.usbBackData(byteArrayToHexStr(data)!!)
                       }
                   }
               }
               mSerialIoManagerOne = SerialInputOutputManager(portOne, mListenerOne) //添加监听
               mSerialIoManagerOne.readBufferSize = 100
               //在新的线程中监听串口的数据变化
               mExecutorOne.submit(mSerialIoManagerOne)
               connectState = true
               return connectState
//               writeData(BleDataMake.makeStartMeterData())
           }
       }
        connectState = false
        return connectState
    }

    fun getConnectionState():Boolean {
        return connectState
    }
    fun writeData(data:String){
        var s1 = HexUtil().hexStringToBytes(data)
        portOne?.write(s1,5000)
    }

    fun byteArrayToHexStr(byteArray: ByteArray?): String? {
        if (byteArray == null) {
            return null
        }
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(byteArray.size * 2)
        for (j in byteArray.indices) {
            val v: Int = byteArray[j] and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
}