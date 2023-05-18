package com.example.lkacmf.util.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.adapter.ArrayAdapter
import com.example.lkacmf.util.*
import com.example.lkacmf.util.ble.BleConnectCallBack
import com.example.lkacmf.util.ble.BleContent
import com.example.lkacmf.util.ble.BleScanCallBack
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
    fun requestPermission(activity: MainActivity) {
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
                        bleFuncation(activity)
                    } else {
                        Log.e("TAG", "您拒绝了如下权限：$deniedList")
                        activity.finish()
                    }
                }
        }
    }

    fun initScanAgainDialog(stater: String, activity: MainActivity) {
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_scan_again,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        if (stater == "scan") {
            dialog.etWorkPipe.hint = activity.resources.getString(R.string.scan_again)
        } else if (stater == "connect") {
            dialog.etWorkPipe.hint = activity.resources.getString(R.string.connect_again)
        }

        dialog.btnCancel.setOnClickListener {
            dialog.dismiss()
            activity.finish()
        }
        dialog.btnSure.setOnClickListener {
            dialog.dismiss()
            dialog = initProgressDialog(activity)
            bleFuncation(activity)
        }
    }

    fun setConfigDialog(activity: MainActivity) {
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
        val gridLayoutManager = GridLayoutManager(activity,4)
        dialog.recyclerView.layoutManager = gridLayoutManager
        var dataList = mutableListOf<String>("1","2","3","4","5","6","7","8")
        var selectList = mutableListOf<Int>(0,3)
        var adapter = ArrayAdapter(activity,dataList,selectList)
        dialog.recyclerView.adapter = adapter

        dialog.btnSettingCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.btnSettingSure.setOnClickListener {
            dialog.dismiss()
           LogUtil.e("TAG","${selectList.size}")
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

    fun bleFuncation(
        activity: MainActivity,
    ) {
        var dialog = initProgressDialog(activity)
        BleContent.initBleScanner(object : BleScanCallBack {
            override fun scanFinish(scanFinish: String) {
                (R.string.scan_finish).showToast(MyApplication.context)
                dialog.dismiss()
                MainDialog().initScanAgainDialog("scan", activity)
            }

            override fun scanFail(scanFail: String) {
                (R.string.scan_fail).showToast(MyApplication.context)
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
                                stater.showToast(activity)
                                if (stater != activity.resources.getString(R.string.connect_success)) {
                                    MainDialog().initScanAgainDialog("connect", activity)
                                    dialog.dismiss()
                                } else {
                                    //连接成功
                                    dialog.dismiss()
                                    MainActivity().writeHandData()
                                }
                            }
                        })
                    }
                }
            }
        })
    }
}