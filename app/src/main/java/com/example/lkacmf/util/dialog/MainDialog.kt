package com.example.lkacmf.util.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.le.ScanResult
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.adapter.ArrayAdapter
import com.example.lkacmf.util.*
import com.example.lkacmf.util.ble.*
import com.example.lkacmf.util.pio.XwpfTUtil
import com.example.lkacmf.util.usb.UsbContent
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.dialog_save_form.*
import kotlinx.android.synthetic.main.dialog_thinkness.*
import kotlinx.android.synthetic.main.setting.*
import java.util.*


class MainDialog {
    /**
     * 初始化重新扫描扫描dialog
     */
    private lateinit var dialog: MaterialDialog

    /**
    权限申请
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun requestPermission(activity: MainActivity): Boolean {
        var permissionTag = false
        val requestList = ArrayList<String>()
        requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        requestList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        if (requestList.isNotEmpty()) {
            PermissionX.init(activity)
                .permissions(requestList)
                .onExplainRequestReason { scope, deniedList ->
                    val message = "需要您同意以下权限才能正常使用"
                    scope.showRequestReasonDialog(deniedList, message, "同意", "取消")
                }
                .request { allGranted, _, deniedList ->
                    if (allGranted) {
                        Log.e("TAG", "所有申请的权限都已通过")
                        permissionTag = true
                    } else {
                        Log.e("TAG", "您拒绝了如下权限：$deniedList")
                        activity.finish()
                    }
                }
        }
        return permissionTag
    }

    /**
     * 设置弹窗
     */
    fun setConfigDialog(activity: Activity) {
        var selectList = mutableListOf<Int>()
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.setting,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        var rate = BaseSharedPreferences.get("rate", "")
        var array = BaseSharedPreferences.get("array", "")
        var userEncoder = BaseSharedPreferences.get("userEncoder", "")
        when (userEncoder) {
            "00" -> {
                dialog.tabLayoutEncode.getTabAt(0)?.select()
            }
            "01" -> {
                dialog.tabLayoutEncode.getTabAt(1)?.select()
            }
        }
        when (rate) {
            "01" -> {
                dialog.tabLayout.getTabAt(0)?.select()
            }
            "05" -> {
                dialog.tabLayout.getTabAt(1)?.select()
            }
            "0A" -> {
                dialog.tabLayout.getTabAt(2)?.select()
            }
        }
        for (i in array.indices) {
            if (array[i].toString() == "0") {
                selectList.add(0)
            } else if (array[i].toString() == "1") {
                selectList.add(1)
            }
        }
        val gridLayoutManager = GridLayoutManager(activity, 4)
        dialog.recyclerView.layoutManager = gridLayoutManager
        var dataList = mutableListOf<String>("笔式", "标准", "三阵列", "五阵列", "七阵列")
        var adapter = ArrayAdapter(activity, dataList, selectList)
        dialog.recyclerView.adapter = adapter

        dialog.btnSettingCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.btnSettingSure.setOnClickListener {
            when (dialog.tabLayoutEncode.selectedTabPosition) {
                0 -> {
                    userEncoder = "00"
                    BaseSharedPreferences.put("userEncoder", "00")
//                    UsbContent.writeData(BleDataMake.makeWriteSettingData())
                }
                1 -> {
                    userEncoder = "01"
                    BaseSharedPreferences.put("userEncoder", "01")
//                    UsbContent.writeData(BleDataMake.makeWriteSettingData())
                }
            }
            when (dialog.tabLayout.selectedTabPosition) {
                0 -> {
                    rate = "01"
                    BaseSharedPreferences.put("rate", "01")
                }
                1 -> {
                    rate = "05"
                    BaseSharedPreferences.put("rate", "05")
                }
                2 -> {
                    rate = "0A"
                    BaseSharedPreferences.put("rate", "0A")
                }
            }
            var item = "00"
            for (i in 0 until selectList.size) {
                item += selectList[i]
            }
            BaseSharedPreferences.put("array", item)
            array = item.toInt(2).toString(16)
            UsbContent.writeData(BleDataMake.makeWriteSettingData(rate,"00",userEncoder))
            dialog.dismiss()

        }
    }

    /**
     * 初始化扫描dialog
     */
    fun initProgressDialog(activity: MainActivity): MaterialDialog {
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.progress_dialog,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        return dialog
    }

    /**
     * 连接
     */
    fun bleFuncation(activity: MainActivity, bleMainConnectCallBack: BleMainConnectCallBack) {
        var dialog = initProgressDialog(activity)
//        var connectTag = ""
        BleContent.initBleScanner(object : BleScanCallBack {
            override fun scanFinish(scanFinish: String) {
                bleMainConnectCallBack.onConnectedfinish()
//                connectTag = MyApplication.context.resources.getString(R.string.scan_finish)
                dialog.dismiss()
            }

            override fun scanFail(scanFail: String) {
                bleMainConnectCallBack.onConnectedfail()
//                connectTag = MyApplication.context.resources.getString(R.string.scan_fail)
                dialog.dismiss()
            }

            @SuppressLint("MissingPermission")
            override fun scanItem(scanResult: ScanResult) {
                if (scanResult.device.name == "E104-BT52-V2.0") {
                    if (BleContent.isScaning()) {
                        BleContent.stopScaning()
                        BleContent.initBleConnector(scanResult, object : BleConnectCallBack {
                            @RequiresApi(Build.VERSION_CODES.O)
                            override fun onConnectedStater(stater: String) {
                                if (stater != activity.resources.getString(R.string.connect_success)) {
//                                    connectTag = MyApplication.context.resources.getString(R.string.connect_again)
                                    bleMainConnectCallBack.onConnectedagain()
                                    dialog.dismiss()
                                } else {
                                    //连接成功
//                                    connectTag = MyApplication.context.resources.getString(R.string.connect_success)
                                    bleMainConnectCallBack.onConnectedsuccess()
                                    dialog.dismiss()
                                }
                            }
                        })
                    }
                }
            }
        })
//        return connectTag
    }

    /**
     * 生成报告
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    fun writeFormDataDialog(activity: Activity, bitmapBX: Bitmap, bitmapBZ: Bitmap, bitmapDX: Bitmap) {
//        CoroutineScope(Dispatchers.Main)
//            .launch {
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_save_form,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        dialog.btnFormCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.btnFormSure.setOnClickListener {
            var person = dialog.etPerson.text.toString()
            var code = dialog.etCode.text.toString()
            var file = dialog.etFile.text.toString()
            var position = dialog.etPosition.text.toString()
            if (person.trim { it <= ' ' } == "") {
                "操作人员不能为空".showToast(activity)
                return@setOnClickListener
            }
            if (code.trim { it <= ' ' } == "") {
                "部件编号不能为空".showToast(activity)
                return@setOnClickListener
            }
            if (file.trim { it <= ' ' } == "") {
                "检测文件不能为空".showToast(activity)
                return@setOnClickListener
            }
            if (position.trim { it <= ' ' } == "") {
                "检测位置不能为空".showToast(activity)
                return@setOnClickListener
            }

            var date = UtcToLocalTime.timeFormatChange()
            //显示截图
            val dataMap: MutableMap<String, Any> = HashMap()
            dataMap["date"] = date
            dataMap["person"] = person
            dataMap["position"] = position
            dataMap["device"] = "ACMF"
            dataMap["code"] = code
            dataMap["describe"] = "describe"
            dataMap["file"] = file
            dataMap["probecode"] = "探头编号"
            dataMap["probefile"] = "探头文件！"

            val templetDocPath = activity.assets.open("acmf.docx")
            var saveFormState = XwpfTUtil.writeDocx(activity, templetDocPath, dataMap, bitmapBX,bitmapBZ,bitmapDX)
            if (saveFormState) {
                MyApplication.context.resources.getString(R.string.save_success).showToast(activity)
                dialog.dismiss()
            } else {
                MyApplication.context.resources.getString(R.string.save_fail).showToast(activity)
                dialog.dismiss()
            }
        }
//            }
    }

    /**
     * 设置图层
     */
    fun setThinkness(activity: Activity, thinknessCallBack: ThinknessCallBack) {
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_thinkness,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        dialog.btnThinknessCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.btnThinknessSure.setOnClickListener {
            var thinkness = dialog.etThinkness.text.toString()
            if (thinkness.trim { it <= ' ' } == "") {
                "图层厚度不能为空".showToast(activity)
                return@setOnClickListener
            }
            thinknessCallBack.thinknessCallBack(thinkness)
            dialog.dismiss()
        }
    }
}