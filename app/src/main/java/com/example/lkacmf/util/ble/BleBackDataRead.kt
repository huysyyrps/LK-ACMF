package com.example.lkacmf.util.ble

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.data.CharacteristicUuid
import com.example.lkacmf.util.BaseSharedPreferences
import com.example.lkacmf.util.BinaryChange
import com.example.lkacmf.util.showToast
import com.example.lkacmf.view.BaseLineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.dialog_hand.*
import kotlinx.android.synthetic.main.dialog_hand_error.*
import java.util.ArrayList

@SuppressLint("StaticFieldLeak")
object BleBackDataRead {
    var deviceCode = ""
    var activatCode = ""
    private lateinit var deviceDate:String
    private lateinit var context: MainActivity
    private lateinit var dialog: MaterialDialog
    fun BleBackDataContext(activity:MainActivity){
        context = activity
    }

    /**
     * 握手读取
     */
    @SuppressLint("StaticFieldLeak")
    fun readHandData(data: String){
        var backData = BinaryChange().hexStringToByte(data)
        //校验
        if (BaseData.hexStringToBytes(data.substring(0,data.length-2))==data.substring(data.length-2,data.length)){
            when {
                backData[4]=="00" -> {
                    LogUtil.e("TAG","未授权")
                    deviceCode = ""
                    deviceDate = ""
                    if (backData.size>20){
                        for (i in 9 ..20){
                            deviceCode+=backData[i]
                        }

                        for (i in 5 ..8){
                            deviceDate+=backData[i]
                        }
                        BaseSharedPreferences.put("deviceDate", deviceDate)
                    }else{
                        R.string.ble_activate_except.showToast(context)
                    }

                    initHandDialog(context)
                }
                backData[4]=="01" -> {
                    LogUtil.e("TAG","授权")
                }
                else -> {
                    initHandErrorDialog(context)
                }
            }
        }
    }

    /**
     * 授权读取
     */
    fun readEmpowerData(data: String){
        var backData = BinaryChange().hexStringToByte(data)
        //校验
        if (BaseData.hexStringToBytes(data.substring(0,data.length-2))==data.substring(data.length-2,data.length)){
            when {
                backData[2]=="00" -> {
                    LogUtil.e("TAG","授权失败")
                    context.resources.getString(R.string.empower_faile).showToast(context)
                }
                backData[2]=="01" -> {
                    LogUtil.e("TAG","授权成功")
                    context.resources.getString(R.string.empower_success).showToast(context)
                    //读取配置
                    BleContent.writeData(
                        BleDataMake().makeReadSettingData(),
                        CharacteristicUuid.ConstantCharacteristicUuid, object : BleWriteCallBack {
                            override fun writeCallBack(writeBackData: String) {
                                LogUtil.e("TAG", "写入数据回调 = $writeBackData")
                            }
                        })
                }
                else -> {
                    initHandErrorDialog(context)
                }
            }
        }
    }


    /**
     * 授权弹窗
     */
    fun initHandDialog(context: Activity) {
        if (deviceCode.trim { it <= ' ' }!=""){
            dialog = MaterialDialog(context)
                .cancelable(false)
                .show {
                    customView(    //自定义弹窗
                        viewRes = R.layout.dialog_hand,//自定义文件
                        dialogWrapContent = true,    //让自定义宽度生效
                        scrollable = true,            //让自定义宽高生效
                        noVerticalPadding = true    //让自定义高度生效
                    )
                    cornerRadius(16f)
                }
            dialog.tvDviceCode.text = deviceCode
            dialog.btnCancel.setOnClickListener {
                dialog.dismiss()
                context.finish()
            }
            dialog.btnSure.setOnClickListener {
                activatCode = dialog.etActivateCode.text.toString()
                if (activatCode.trim { it <= ' ' } == "" || activatCode.trim { it <= ' ' }.length!=24) {
                    R.string.please_write_ture_activatecode .showToast(context)
                    return@setOnClickListener
                }else{
                    deviceDate = BaseSharedPreferences.get("deviceDate","")
                    if (deviceDate.trim { it <= ' ' }==""){
                        R.string.dont_have_finish_date.showToast(context)
                    }else{
                        BleContent.writeData(
                            BleDataMake().makeEmpowerData(deviceDate,activatCode),
                            CharacteristicUuid.ConstantCharacteristicUuid,
                            object : BleWriteCallBack {
                                override fun writeCallBack(writeBackData: String) {
                                    LogUtil.e("TAG", "写入数据回调 = $writeBackData")
                                }
                            })
                        dialog.dismiss()
                    }
                }
            }
        }else{
            R.string.ble_activate_except.showToast(context)
        }
    }

    /**
     * 激活错误弹窗
     */
    fun initHandErrorDialog(context: Context) {
        dialog = MaterialDialog(context)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_hand_error,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        dialog.btnErrorCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.btnErrorSure.setOnClickListener {
            dialog.dismiss()
        }
    }


    /**
     * 读取配置信息
     */
    fun readSettingData(data: String){
        var backData = BinaryChange().hexStringToByte(data)
        //校验
        if (BaseData.hexStringToBytes(data.substring(0,data.length-2))==data.substring(data.length-2,data.length)){
            when {
                backData[2]=="00" -> {
                    LogUtil.e("TAG","读取成功")
                    //hexString转10进制
                    var rate = String.format("%02x",backData[3].toInt(16)).toUpperCase()
                    var array = backData[4].toInt(16).toString(2)
                    while (array.length<8){
                        array = "0${array}"
                    }
                    BaseSharedPreferences.put("rate", rate)
                    BaseSharedPreferences.put("array", array)
                }
                backData[2]=="01" -> {
                    LogUtil.e("TAG","设置成功")
                }
                else -> {
                    initHandErrorDialog(context)
                }
            }
        }
    }

    /**
     * 测量信息
     */
    fun meterData(data: String):String{
        var backData = BinaryChange().hexStringToByte(data)
        //校验
        if (BaseData.hexStringToBytes(data.substring(0,data.length-2))==data.substring(data.length-2,data.length)){
            when {
                backData[2]=="00" -> {
                    return "00"
                }
                backData[2]=="01" -> {
                    return "01"
                }
                backData[2]=="02" -> {
                    return "02"
                }
            }
        }
        return "00"
    }

    /**
     * 测量信息
     */
    fun readMeterData(data: String, lineChartBX: BaseLineChart){
        lateinit var lineBXSet: LineDataSet
        lateinit var lineBZSet: LineDataSet
        var landBXList: ArrayList<Entry> = ArrayList()
        var backData = BinaryChange().hexStringToByte(data)
        //校验
//                            if (BaseData.hexStringToBytes(readData.substring(0,readData.length-2))==readData.substring(readData.length-2,readData.length)){
//
//                            }
        if (backData[2]=="00"){

        }else{
            var xHex = "${backData[3]}${backData[4]}${backData[5]}${backData[6]}".toInt(16)
            var xData = (xHex.toLong()/1000).toFloat()

            var yBXHex = "${backData[8]}${backData[9]}${backData[10]}${backData[11]}".toInt(16)
            var yBXData = BinaryChange().ieee754ToFloat(yBXHex)

            var yBZHex = "${backData[12]}${backData[13]}${backData[14]}${backData[15]}".toInt(16)
            var yBZData =  BinaryChange().ieee754ToFloat(yBZHex)

            landBXList.add(Entry(xData, yBXData))

            lineBXSet = LineDataSet(landBXList, "BX")
            //不绘制数据
            lineBXSet.setDrawValues(false)
            //不绘制圆形指示器
            lineBXSet.setDrawCircles(false)
            //线模式为圆滑曲线（默认折线）
            //lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineBXSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
            //将数据集添加到数据 ChartData 中
            val lineData = LineData(lineBXSet)
            //将数据添加到图表中
            lineChartBX.data = lineData
            lineChartBX.notifyDataSetChanged()
            lineChartBX.invalidate()
        }
    }
}