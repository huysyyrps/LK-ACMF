package com.example.lkacmf.util.usb

import android.hardware.usb.UsbManager
import androidx.appcompat.app.AppCompatActivity
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
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
    fun usbDeviceConstant(content: MainActivity, usbBackDataLisition: UsbBackDataLisition){
        val manager = content.getSystemService(AppCompatActivity.USB_SERVICE) as UsbManager
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            content.resources.getString(R.string.no_devices).showToast(content)
            return
        }
        val driverOne = availableDrivers[0]
//        val driverMore = availableDrivers[1]
        val connectionOne = manager.openDevice(driverOne.device) ?:return
//        val connectionMore = manager.openDevice(driverMore.device) ?:return
        portOne = driverOne.ports[0]
//        portMore = driverMore.ports[0]// Most devices have just one port (port 0)
        portOne?.open(connectionOne)
//        portMore?.open(connectionMore)
        //设置串口的波特率、数据位，停止位，校验位115200
        portOne?.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
//        portMore?.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        val mExecutorOne: ExecutorService = Executors.newSingleThreadExecutor()
//        val mExecutorMore: ExecutorService = Executors.newSingleThreadExecutor()
        val mSerialIoManagerOne: SerialInputOutputManager
//        val mSerialIoManagerMore: SerialInputOutputManager
        val mListenerOne: SerialInputOutputManager.Listener = object : SerialInputOutputManager.Listener {
            override fun onRunError(e: Exception) {
                LogUtil.e("TAG", "Runner stopped.")
            }
            override fun onNewData(data: ByteArray) {
                //TODO 新的数据
                val str = String(data)
                byteArrayToHexStr(data)?.let {
//                    LogUtil.e("TAG", it)
                    usbBackDataLisition.usbBackData(byteArrayToHexStr(data)!!)
                }
            }
        }
//        val mListenerMore: SerialInputOutputManager.Listener = object : SerialInputOutputManager.Listener {
//            override fun onRunError(e: Exception) {
//                LogUtil.e("TAG", "Runner stopped.")
//                "Runner stopped.".showToast(content)
//            }
//
//            override fun onNewData(data: ByteArray) {
//                //TODO 新的数据
//                val str = String(data)
//                usbBackDataLisition.usbBackData(str)
//            }
//        }
        mSerialIoManagerOne = SerialInputOutputManager(portOne, mListenerOne) //添加监听
        mSerialIoManagerOne.readBufferSize = 100
//        mSerialIoManagerMore = SerialInputOutputManager(portMore, mListenerMore)
        //在新的线程中监听串口的数据变化
        mExecutorOne.submit(mSerialIoManagerOne)
//        mExecutorMore.submit(mSerialIoManagerMore)
    }
    fun writeData(){
        portOne?.write("11".getByteArray(),5000)
        portMore?.write("22".getByteArray(),5000)
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