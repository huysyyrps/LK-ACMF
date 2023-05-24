package com.example.lkacmf.util.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.adapter.ArrayAdapter
import com.example.lkacmf.data.CharacteristicUuid
import com.example.lkacmf.util.*
import com.example.lkacmf.util.ble.*
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.dialog_scan_again.*
import kotlinx.android.synthetic.main.setting.*
import kotlin.collections.ArrayList

class MainDialog {
    /**
     * 初始化重新扫描扫描dialog
     */
    private lateinit var dialog: MaterialDialog

    /**
    权限申请
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun requestPermission(activity: MainActivity):Boolean {
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
    fun setConfigDialog(activity: MainActivity) {
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
        when(rate){
            "01"->{
                dialog.tabLayout.getTabAt(0)?.select()
            }
            "05"->{
                dialog.tabLayout.getTabAt(1)?.select()
            }
            "0A"->{
                dialog.tabLayout.getTabAt(2)?.select()
            }
        }
        for (i in array.indices){
            if (array[i].toString()=="0"){
                selectList.add(0)
            }else  if (array[i].toString()=="1"){
                selectList.add(1)
            }
        }
        val gridLayoutManager = GridLayoutManager(activity,4)
        dialog.recyclerView.layoutManager = gridLayoutManager
        var dataList = mutableListOf<String>("1","2","3","4","5","6","7","8")
        var adapter = ArrayAdapter(activity,dataList,selectList)
        dialog.recyclerView.adapter = adapter

        dialog.btnSettingCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.btnSettingSure.setOnClickListener {
            when(dialog.tabLayout.selectedTabPosition){
                0->{
                    rate = "01"
                    BaseSharedPreferences.put("rate", "01")
                }
                1->{
                    rate = "05"
                    BaseSharedPreferences.put("rate", "05")
                }
                2->{
                    rate = "0A"
                    BaseSharedPreferences.put("rate", "0A")
                }
            }
            var item = ""
            for (i in 0 until selectList.size){
                item += selectList[i]
            }
            BaseSharedPreferences.put("array", item)
            array = item.toInt(2).toString(16)
            BleContent.writeData(
                BleDataMake().makeWriteSettingData(rate,array),
                CharacteristicUuid.ConstantCharacteristicUuid, object : BleWriteCallBack {
                    override fun writeCallBack(writeBackData: String) {
                        LogUtil.e("TAG", "写入设置数据回调 = $writeBackData")
                    }
                })
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
    fun writeFormDataDialog(activity: MainActivity, bleMainConnectCallBack: BleMainConnectCallBack) {
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
    }
}