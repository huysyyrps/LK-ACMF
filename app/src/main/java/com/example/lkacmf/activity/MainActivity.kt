package com.example.lkacmf.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.example.lkacmf.MyApplication.Companion.context
import com.example.lkacmf.R
import com.example.lkacmf.util.*
import com.example.lkacmf.network.DownloadApk
import com.example.lkacmf.util.dialog.MainDialog
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_scan_again.*
import kotlinx.android.synthetic.main.drawer_item.view.*


class MainActivity : BaseActivity(), View.OnClickListener {
    //dialog
    private lateinit var dialog : MaterialDialog
    private lateinit var serviceUuid : String
    private var isConnect : Boolean = false
    private var version : String = "1.0.0"

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
        MainDialog().initProgressDialog(this)
        btn.setOnClickListener(this)
        imageView.setOnClickListener(this)
        linSetting.setOnClickListener(this)
        linVersionCheck.setOnClickListener(this)
        linContactComp.setOnClickListener(this)
        btnFinish.setOnClickListener(this)
        version = BaseProjectVersion().getPackageInfo(context)?.versionName.toString()
        linCurrentVersion.tvVersion.text = version
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn->{
                if (isConnect) {
                    BleContent.writeData("1234", serviceUuid, object : BleWriteCallBack {
                        override fun writeCallBack(writeBackData: String) {
                            TODO("Not yet implemented")
                        }

                    })
                } else {
                    (R.string.connect_ble).showToast(this)
                }
            }
            R.id.imageView->{
                drawer_layout.openDrawer(GravityCompat.START)
            }
            R.id.linSetting->{
            }
            R.id.linVersionCheck->{
                DownloadApk().downloadApk(this,version)
            }
            R.id.linContactComp->{
                BaseTelPhone.telPhone(this)

            }
            R.id.btnFinish->{
                finish()
            }
        }
    }

//    /**
//     * 初始化扫描dialog
//     */
//    private fun initProgressDialog(){
//        dialog = MaterialDialog(this)
//            .cancelable(false)
//            .show{
//            customView(	//自定义弹窗
//                viewRes = R.layout.progress_dialog,//自定义文件
//                dialogWrapContent = true,	//让自定义宽度生效
//                scrollable = true,			//让自定义宽高生效
//                noVerticalPadding = true    //让自定义高度生效
//            )
//            cornerRadius(16f)
//        }
//    }

//    /**
//     * 初始化重新扫描扫描dialog
//     */
//    private fun initScanAgainDialog(stater:String){
//        dialog = MaterialDialog(this)
//            .cancelable(false)
//            .show{
//                customView(	//自定义弹窗
//                    viewRes = R.layout.dialog_scan_again,//自定义文件
//                    dialogWrapContent = true,	//让自定义宽度生效
//                    scrollable = true,			//让自定义宽高生效
//                    noVerticalPadding = true    //让自定义高度生效
//                )
//                cornerRadius(16f)
//            }
//        if (stater=="scan"){
//            dialog.etWorkPipe.hint =  resources.getString(R.string.scan_again)
//        }else if (stater=="connect"){
//            dialog.etWorkPipe.hint =  resources.getString(R.string.connect_again)
//        }
//
//        dialog.btnCancel.setOnClickListener{
//            dialog.dismiss()
//            finish()
//        }
//        dialog.btnSure.setOnClickListener{
//            dialog.dismiss()
//            initProgressDialog()
//            bleFuncation()
//        }
//    }

    /**
        权限申请
    */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestPermission() {
        val requestList = ArrayList<String>()
        requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        requestList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        if (requestList.isNotEmpty()) {
            PermissionX.init(this)
                .permissions(requestList)
                .onExplainRequestReason { scope, deniedList ->
                    val message = "需要您同意以下权限才能正常使用"
                    scope.showRequestReasonDialog(deniedList, message, "同意", "取消")
                }
                .request { allGranted, _, deniedList ->
                    if (allGranted) {
                        Log.e("TAG", "所有申请的权限都已通过")
                        MainDialog().bleFuncation(this)
                    } else {
                        Log.e("TAG", "您拒绝了如下权限：$deniedList")
                        finish()
                    }
                }
        }
    }

//    private fun bleFuncation(){
//        BleContent.initBleScanner(object : BleScanCallBack{
//            override fun scanFinish(scanFinish: String) {
//                (R.string.scan_finish).showToast(MyApplication.context)
//                dialog.dismiss()
//                MainDialog().initScanAgainDialog("scan",this@MainActivity)
//            }
//
//            override fun scanFail(scanFail: String) {
//                (R.string.scan_fail).showToast(MyApplication.context)
//                dialog.dismiss()
//            }
//
//            @SuppressLint("MissingPermission")
//            override fun scanItem(scanResult: ScanResult) {
//                if (scanResult.device.name == "E104-BT52-V2.0") {
//                    if (BleContent.isScaning()) {
//                        BleContent.stopScaning()
//                        BleContent.initBleConnector(scanResult,object : BleConnectCallBack{
//                            override fun onConnectedStater(stater: String) {
//                                stater.showToast(this@MainActivity)
//                                dialog.dismiss()
//                                if (stater!=this@MainActivity.resources.getString(R.string.connect_success)){
//                                    MainDialog().initScanAgainDialog("connect",this@MainActivity)
//                                    isConnect = false
//                                }else{
//                                    isConnect = true
//                                    serviceUuid =
//                                        scanResult.scanRecord?.serviceUuids?.get(0)?.toString().toString()
//                                    serviceUuid.let {
//                                        BleContent.readData(it,object : BleReadCallBack{
//                                            override fun readCallBack(readData: String) {
//                                                LogUtil.e("TAG","通知数据 = $readData")
//                                            }
//
//                                        })
//                                    }
//                                }
//                            }
//                        })
//                    }
//                }
//            }
//
//        })
//    }

    override fun onDestroy() {
        super.onDestroy()
        BleContent.releaseBleScanner()
    }
}