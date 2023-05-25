package com.example.lkacmf.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.MyApplication.Companion.context
import com.example.lkacmf.R
import com.example.lkacmf.data.CharacteristicUuid
import com.example.lkacmf.network.DownloadApk
import com.example.lkacmf.util.*
import com.example.lkacmf.util.ble.*
import com.example.lkacmf.util.dialog.MainDialog
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.mediaprojection.CaptureImage
import com.github.mikephil.charting.data.Entry
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_scan_again.*
import kotlinx.android.synthetic.main.drawer_item.view.*
import java.util.*


class MainActivity : BaseActivity(), View.OnClickListener {
    private var version: String = "1.0.0"
    private var isStart: Boolean = false
    private var iswrite: Boolean = false

    /**
     * 初始化重新扫描扫描dialog
     */
    private lateinit var dialog: MaterialDialog

    private var landBXList: ArrayList<Entry> = ArrayList()
    private var landBZList: ArrayList<Entry> = ArrayList()
    private var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var mediaManager: MediaProjectionManager
    private var mMediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private var isGot: Boolean = false

    private val tabItemStr = arrayListOf<String>().apply {
        add(context.resources.getString(R.string.start))
        add(context.resources.getString(R.string.stop))
        add(context.resources.getString(R.string.refresh))
        add(context.resources.getString(R.string.reset))
        add(context.resources.getString(R.string.detection_mode))
        add(context.resources.getString(R.string.calibration))
        add(context.resources.getString(R.string.measure))
        add(context.resources.getString(R.string.forms))
        add(context.resources.getString(R.string.save))
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
            tbLayout.addTab(tab, index, false)
        }
        //tabLayout选择监听
        tabLayoutSelect()
        BleBackDataRead.BleBackDataContext(this)
        if (!bluetoothAdapter.isEnabled) {
            activityResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        } else {
            //是否通过全部权限
            var permissionTag = MainDialog().requestPermission(this)
            if (permissionTag) {
                //是否连接成功
                MainDialog().bleFuncation(this@MainActivity, object : BleMainConnectCallBack {
                    override fun onConnectedfinish() {
                        (R.string.scan_finish).showToast(context)
                        initScanAgainDialog("scan", this@MainActivity)
                    }

                    override fun onConnectedfail() {
                        (R.string.scan_fail).showToast(context)
                    }

                    override fun onConnectedsuccess() {
                        resources.getString(R.string.connect_success).showToast(this@MainActivity)
                        writeHandData()
                    }

                    override fun onConnectedagain() {
                        initScanAgainDialog("connect", this@MainActivity)

                    }

                })
            }
        }

        imageView.setOnClickListener(this)
        linSetting.setOnClickListener(this)
        linVersionCheck.setOnClickListener(this)
        linContactComp.setOnClickListener(this)
        btnFinish.setOnClickListener(this)
        version = BaseProjectVersion().getPackageInfo(context)?.versionName.toString()
        linCurrentVersion.tvVersion.text = version

        LineChartSetting().SettingLineChart(lineChartBX)
        LineChartSetting().SettingLineChart(lineChartBZ)
    }

    /**
     * 扫描弹窗
     */
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
            MainDialog().bleFuncation(this@MainActivity, object : BleMainConnectCallBack {
                override fun onConnectedfinish() {
                    (R.string.scan_finish).showToast(context)
                    initScanAgainDialog("scan", this@MainActivity)
                }

                override fun onConnectedfail() {
                    (R.string.scan_fail).showToast(context)
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onConnectedsuccess() {
                    resources.getString(R.string.connect_success).showToast(this@MainActivity)
                    writeHandData()
                }

                override fun onConnectedagain() {
                    initScanAgainDialog("connect", this@MainActivity)

                }

            })
        }
    }

    //写入数据
    @RequiresApi(Build.VERSION_CODES.O)
    fun writeHandData() {
        Thread.sleep(1500)
        BleContent.writeData(
            BleDataMake().makeHandData(),
            CharacteristicUuid.ConstantCharacteristicUuid, object : BleWriteCallBack {
                override fun writeCallBack(writeBackData: String) {
                    LogUtil.e("TAG", "写入数据回调 = $writeBackData")
                    iswrite = true
                    ReadData()
                }
            })
    }

    //读取数据
    fun ReadData() {
        if (iswrite) {
            BleContent.readData(
                CharacteristicUuid.ConstantCharacteristicUuid,
                object : BleReadCallBack {
                    override fun readCallBack(readData: String) {
                        //帧头和识别码对应才能解析  读取握手信息
                        if (readData.length > 4 && readData.substring(0, 4) == "BE01") {
                            BleBackDataRead.readHandData(readData)
                        } else if (readData.length > 4 && readData.substring(0, 4) == "BE02") {
                            BleBackDataRead.readEmpowerData(readData)
                        } else if (readData.length > 4 && readData.substring(0, 4) == "BE03") {
                            BleBackDataRead.readSettingData(readData)
                        } else if (readData.length > 4 && readData.substring(0, 4) == "BE14") {
                            //判断是否测量
                            var meterTag = BleBackDataRead.meterData(readData)
                            when (meterTag) {
                                "00" -> {
                                    LogUtil.e("TAG", meterTag)
                                }
                                "01" -> {
                                    LogUtil.e("TAG", "开始")
                                }
                                "02" -> {
                                    LogUtil.e("TAG", "复位")
                                }
                            }
                        } else if (readData.length > 4 && readData.substring(0, 4) == "BE04") {
                            LogUtil.e("TAG", readData)
                            BleBackDataRead.readMeterData(readData, lineChartBX)
                        }
                    }
                })
        }
    }

    //开启蓝牙
    @RequiresApi(Build.VERSION_CODES.S)
    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (bluetoothAdapter.isEnabled) {
                    MainDialog().requestPermission(this)
                } else {
                    R.string.ble_open_fail.showToast(context)
                }
            }
        }

    private fun tabLayoutSelect() {
        tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    context.resources.getString(R.string.start) -> {
                        isStart = true
                        BleContent.writeData(
                            BleDataMake().makeStartMeterData(),
                            CharacteristicUuid.ConstantCharacteristicUuid,
                            object : BleWriteCallBack {
                                override fun writeCallBack(writeBackData: String) {
                                    LogUtil.e("TAG", "写入开始测量回调 = $writeBackData")
                                }
                            })
                    }
                    context.resources.getString(R.string.stop) -> {
                        isStart = false
                        BleContent.writeData(
                            BleDataMake().makeStopMeterData(),
                            CharacteristicUuid.ConstantCharacteristicUuid,
                            object : BleWriteCallBack {
                                override fun writeCallBack(writeBackData: String) {
                                    LogUtil.e("TAG", "写入开始测量回调 = $writeBackData")
                                }
                            })
                    }
                    context.resources.getString(R.string.refresh) -> {
                        BleBackDataRead.readRefreshData(lineChartBX)
                        isStart = true
                        tbLayout.selectTab(tbLayout.getTabAt(0))
                    }
                    context.resources.getString(R.string.reset) -> {
                        BleBackDataRead.readRefreshData(lineChartBX)
                        isStart = true
                        tbLayout.selectTab(tbLayout.getTabAt(0))
                    }
                    context.resources.getString(R.string.forms) -> {
                        mediaManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                        if (mMediaProjection==null){
                            val captureIntent: Intent =
                                mediaManager.createScreenCaptureIntent()
                            startActivityForResult(captureIntent, Constant.TAG_ONE)
                        }else{
                            mMediaProjection?.let { CaptureImage().captureImages(this@MainActivity,"form", it) }
                        }
                    }
                    context.resources.getString(R.string.save) -> {
                        mediaManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                        if (mMediaProjection==null){
                            val captureIntent: Intent =
                                mediaManager.createScreenCaptureIntent()
                            startActivityForResult(captureIntent, Constant.TAG_TWO)
                        }else{
                            mMediaProjection?.let { CaptureImage().captureImages(this@MainActivity,"image", it) }
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constant.TAG_ONE -> {
                    mMediaProjection = data?.let { mediaManager.getMediaProjection(resultCode, it) }
                    mMediaProjection?.let { CaptureImage().captureImages(this,"form", it) }
                }
                Constant.TAG_TWO -> {
                    mMediaProjection = data?.let { mediaManager.getMediaProjection(resultCode, it) }
                    mMediaProjection?.let { CaptureImage().captureImages(this,"image", it) }
                }
            }
        }
    }



    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageView -> {
                drawer_layout.openDrawer(GravityCompat.START)
            }
            R.id.linSetting -> {
                MainDialog().setConfigDialog(this)
            }
            R.id.linVersionCheck -> {
                DownloadApk().downloadApk(this, version)
            }
            R.id.linContactComp -> {
                BaseTelPhone.telPhone(this)

            }
            R.id.btnFinish -> {
                finish()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        BleContent.releaseBleScanner()
    }
}