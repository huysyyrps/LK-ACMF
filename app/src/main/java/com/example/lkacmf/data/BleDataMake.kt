package com.example.lkacmf.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.lkacmf.util.BaseData

class BleDataMake {
    @RequiresApi(Build.VERSION_CODES.O)
    fun  makeProofData():String{
        return "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.CONNECTCODE}${BaseData.getYearTop()}${BaseData.getYearBotton()}${BaseData.getMonth()}${BaseData.getDay()}"
    }
}