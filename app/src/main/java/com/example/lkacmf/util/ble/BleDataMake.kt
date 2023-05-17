package com.example.lkacmf.util.ble

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.lkacmf.data.CharacteristicUuid
import com.example.lkacmf.util.BinaryChange

class BleDataMake {
    /**
     * 握手
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun  makeHandData():String{
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.CONNECTCODE}${BaseData.getYearTop()}${BaseData.getYearBotton()}${BaseData.getMonth()}${BaseData.getDay()}"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }

    /**
     * 授权
     */
    fun  makeEmpowerData(deviceDate:String, activatCode:String):String{
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.EMPOWERCODE}${activatCode}${deviceDate}"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }
}