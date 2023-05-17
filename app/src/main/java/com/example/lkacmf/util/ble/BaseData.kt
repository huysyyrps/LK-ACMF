package com.example.lkacmf.util.ble

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.lkacmf.util.BinaryChange
import java.util.*

object BaseData {
    /**
     * 获取年月日
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getYearTop():String{
        var year = Calendar.getInstance().get(Calendar.YEAR).toString()
        return BinaryChange().tenToHex(year.substring(0,2).toInt())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getYearBotton():String{
        var year = Calendar.getInstance().get(Calendar.YEAR).toString()
        return BinaryChange().tenToHex(year.substring(2,4).toInt())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonth():String{
        var month = BinaryChange().tenToHex((Calendar.getInstance().get(Calendar.MONTH)+1))
        var hexMonth = if (month.length<2) {
            "0$month"
        }else{
            month
        }
        return hexMonth.uppercase(Locale.getDefault())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDay():String{
        var day = BinaryChange().tenToHex(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        var hexDay = if (day.length<2) {
            "0$day"
        }else{
            day
        }
        return hexDay.uppercase(Locale.getDefault())
    }

    /**
     * 获取校验码
     */
    open fun hexStringToBytes(hexString: String?): String? {
        var hexString = hexString
        if (hexString == null || hexString == "") {
            return null
        }
        hexString = hexString.trim { it <= ' ' }
        hexString = hexString.uppercase()
        val length = hexString.length / 2
        var ad = 0
        for (i in 0 until length) {
            val pos = i * 2
            ad += Integer.valueOf(hexString.substring(pos,pos+2),16)
        }
        var checkData = Integer.toHexString(ad)
        if (checkData.length > 2) {
            checkData = checkData.substring(checkData.length - 2, checkData.length)
        }
        if (checkData.length == 1) {
            checkData = "0$checkData"
        }
        return checkData.uppercase()
    }
}
