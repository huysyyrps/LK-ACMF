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
import kotlinx.android.synthetic.main.drawer_item.view.*


class MainActivity : BaseActivity(), View.OnClickListener {
    //dialog
    private lateinit var serviceUuid : String
    private var isConnect : Boolean = false
    private var version : String = "1.0.0"
    private lateinit var dialog : MaterialDialog

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
//        dialog = MainDialog().initProgressDialog(this)
//        requestPermission()
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
                        MainDialog().bleFuncation(this,dialog)
                    } else {
                        Log.e("TAG", "您拒绝了如下权限：$deniedList")
                        finish()
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BleContent.releaseBleScanner()
    }
}