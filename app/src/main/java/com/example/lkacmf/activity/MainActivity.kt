package com.example.lkacmf.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.example.lkacmf.MyApplication.Companion.context
import com.example.lkacmf.R
import com.example.lkacmf.network.DownloadApk
import com.example.lkacmf.util.*
import com.example.lkacmf.util.dialog.MainDialog
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_item.view.*


class MainActivity : BaseActivity(), View.OnClickListener {
    //dialog
    private lateinit var serviceUuid : String
    private var isConnect : Boolean = false
    private var version : String = "1.0.0"
    private lateinit var dialog : MaterialDialog
    private val tabItemStr = arrayListOf<String>().apply {
        add("开始")
        add("停止")
        add("刷新")
        add("复位")
        add("检测模式")
        add("标定")
        add("测量")
        add("报表")
        add("保存")
    }

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
        tabItemStr.forEachIndexed { index, value ->
            val tab = tbLayout.newTab()
            tab.text = value
            tbLayout.addTab(tab,index,false)
        }
        //tabLayout选择监听
        tabLayoutSelect()

//        dialog = MainDialog().initProgressDialog(this)
//        MainDialog().requestPermission(this,dialog)

        btn.setOnClickListener(this)
        imageView.setOnClickListener(this)
        linSetting.setOnClickListener(this)
        linVersionCheck.setOnClickListener(this)
        linContactComp.setOnClickListener(this)
        btnFinish.setOnClickListener(this)
        version = BaseProjectVersion().getPackageInfo(context)?.versionName.toString()
        linCurrentVersion.tvVersion.text = version
    }

    private fun tabLayoutSelect() {
        tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Toast.makeText(this@MainActivity, tab.text, Toast.LENGTH_SHORT).show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
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


    override fun onDestroy() {
        super.onDestroy()
        BleContent.releaseBleScanner()
    }
}