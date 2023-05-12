package com.example.lkacmf.util.dialog

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.util.*
import kotlinx.android.synthetic.main.dialog_scan_again.*

class MainDialog {
    /**
     * 初始化重新扫描扫描dialog
     */
    private lateinit var dialog : MaterialDialog
    private lateinit var serviceUuid : String
    private var isConnect : Boolean = false
    fun initScanAgainDialog(stater: String, activity: MainActivity){
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show{
                customView(	//自定义弹窗
                    viewRes = R.layout.dialog_scan_again,//自定义文件
                    dialogWrapContent = true,	//让自定义宽度生效
                    scrollable = true,			//让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        if (stater=="scan"){
            dialog.etWorkPipe.hint =  activity.resources.getString(R.string.scan_again)
        }else if (stater=="connect"){
            dialog.etWorkPipe.hint =  activity.resources.getString(R.string.connect_again)
        }

        dialog.btnCancel.setOnClickListener{
            dialog.dismiss()
            activity.finish()
        }
        dialog.btnSure.setOnClickListener{
            dialog.dismiss()
            dialog = initProgressDialog(activity)
            bleFuncation(activity,dialog)
        }
    }

    /**
     * 初始化扫描dialog
     */
    fun initProgressDialog(activity: MainActivity):MaterialDialog {
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show{
                customView(	//自定义弹窗
                    viewRes = R.layout.progress_dialog,//自定义文件
                    dialogWrapContent = true,	//让自定义宽度生效
                    scrollable = true,			//让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        return dialog
    }

    fun bleFuncation(activity: MainActivity, dialog: MaterialDialog) {
        BleContent.initBleScanner(object : BleScanCallBack {
            override fun scanFinish(scanFinish: String) {
                (R.string.scan_finish).showToast(MyApplication.context)
                dialog.dismiss()
                MainDialog().initScanAgainDialog("scan",activity)
            }

            override fun scanFail(scanFail: String) {
                (R.string.scan_fail).showToast(MyApplication.context)
                this@MainDialog.dialog.dismiss()
            }

            @SuppressLint("MissingPermission")
            override fun scanItem(scanResult: ScanResult) {
                if (scanResult.device.name == "E104-BT52-V2.0") {
                    if (BleContent.isScaning()) {
                        BleContent.stopScaning()
                        BleContent.initBleConnector(scanResult,object : BleConnectCallBack {
                            override fun onConnectedStater(stater: String) {
                                stater.showToast(activity)
                                this@MainDialog.dialog.dismiss()
                                if (stater!=activity.resources.getString(R.string.connect_success)){
                                    MainDialog().initScanAgainDialog("connect",activity)
                                    isConnect = false
                                }else{
                                    isConnect = true
                                    serviceUuid =
                                        scanResult.scanRecord?.serviceUuids?.get(0)?.toString().toString()
                                    serviceUuid.let {
                                        BleContent.readData(it,object : BleReadCallBack {
                                            override fun readCallBack(readData: String) {
                                                LogUtil.e("TAG","通知数据 = $readData")
                                            }

                                        })
                                    }
                                }
                            }
                        })
                    }
                }
            }

        })
    }
}