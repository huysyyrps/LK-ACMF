package com.example.lkacmf.util.ble

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.data.CharacteristicUuid
import com.example.lkacmf.util.BaseSharedPreferences
import com.example.lkacmf.util.BinaryChange
import com.example.lkacmf.util.showToast
import kotlinx.android.synthetic.main.dialog_hand.*
import kotlinx.android.synthetic.main.dialog_hand_error.*

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
                }
                backData[2]=="01" -> {
                    LogUtil.e("TAG","设置成功")
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
}