package com.example.lkacmf.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.MyApplication.Companion.context
import com.example.lkacmf.R
import com.example.lkacmf.entity.VersionInfo
import com.example.lkacmf.module.VersionInfoContract
import com.example.lkacmf.presenter.VersionInfoPresenter
import com.example.lkacmf.util.*
import com.example.lkacmf.util.ble.BaseData
import com.example.lkacmf.util.ble.BleBackDataRead
import com.example.lkacmf.util.ble.BleDataMake
import com.example.lkacmf.util.dialog.MainDialog
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.mediaprojection.CaptureImage
import com.example.lkacmf.util.usb.UsbBackDataLisition
import com.example.lkacmf.util.usb.UsbContent
import com.example.lkacmf.util.usb.UsbContent.writeData
import com.github.mikephil.charting.components.YAxis
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import constant.UiType
import kotlinx.android.synthetic.main.activity_main.*
import listener.OnInitUiListener
import model.UiConfig
import model.UpdateConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import update.UpdateAppUtils


class MainActivity : BaseActivity(), View.OnClickListener, VersionInfoContract.View {
    private var version: String = "1.0.0"
    private lateinit var mediaManager: MediaProjectionManager
    private var mMediaProjection: MediaProjection? = null
    private val tabItemStr = arrayListOf<String>().apply {
        add(context.resources.getString(R.string.start))
        add(context.resources.getString(R.string.stop))
        add(context.resources.getString(R.string.refresh))
        add(context.resources.getString(R.string.reset))
        add(context.resources.getString(R.string.calibration))
        add(context.resources.getString(R.string.measure))
        add(context.resources.getString(R.string.forms))
        add(context.resources.getString(R.string.save))
        add(context.resources.getString(R.string.play_back))
    }
    var axisMaximum: Float = 5.0F
    private lateinit var leftYAxis: YAxis
    var isRoll: Boolean = false
    private lateinit var versionInfoPresenter: VersionInfoPresenter
    public var selectTag:String = ""

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
        versionInfoPresenter = VersionInfoPresenter(this, view = this)
        tabItemStr.forEachIndexed { index, value ->
            val tab = tbLayout.newTab()
            tab.text = value
            tbLayout.addTab(tab, index, false)
        }
        //tabLayout选择监听
        tabLayoutSelect()

        imageView.setOnClickListener(this)
        linSetting.setOnClickListener(this)
        linImageList.setOnClickListener(this)
        linFileList.setOnClickListener(this)
        linVersionCheck.setOnClickListener(this)
        linContactComp.setOnClickListener(this)
        btnFinish.setOnClickListener(this)
        version = ClientVersion.getVersion(applicationContext)
        tvCurrentVersion.text = version

        LineChartSetting().SettingLineChart(this, lineChartBX, true)
        LineChartSetting().SettingLineChart(this, lineChartBZ, true)
        LineChartSetting().SettingMyLineChart(this, lineChart, true)
        val xAxis = lineChartBZ.xAxis
        xAxis.textColor = context.resources.getColor(R.color.theme_back_color)


        var permissionTag = MainDialog().requestPermission(this)
        if (permissionTag) {
            UsbContent.usbDeviceConstant(this, object : UsbBackDataLisition {
                override fun usbBackData(data: String) {
                    if (data.length > 4 && data.substring(0, 4) == "BE06") {
                        if (BaseData.hexStringToBytes(data.substring(0, data.length - 6)) == data.substring(data.length - 6, data.length - 4) && data.length == 38) {
                            BleBackDataRead.readMeterData(data, lineChartBX, lineChartBZ, lineChart, isRoll)
                        }
                    }
                    if (data.length > 4 && data.substring(0, 4) == "BE05") {
                        LogUtil.e("TAG", data)
                        if (BaseData.hexStringToBytes(data.substring(0, data.length - 2)) == data.substring(data.length - 2, data.length) && data.length == 14) {
                            BleBackDataRead.readSettingData(data)
                        }
                    }
                    if (data.length > 4 && data.substring(0, 4) == "BE16") {
                        LogUtil.e("TAG", data)
                        if (selectTag=="reset"){
                            if (UsbContent.connectState) {
                                writeData(BleDataMake.makeReSetMeterData())
                                selectTag=""
                            }
                        }
                    }
                }

            })
        } else {
            R.string.no_permission.showToast(this)
        }

        //获取电量
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        var receiver = BatteryReceiver()
        registerReceiver(receiver, filter)
        if (UsbContent.connectState) {
            writeData(BleDataMake.makeReadSettingData())
        }
    }

    private fun tabLayoutSelect() {
        tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    context.resources.getString(R.string.start) -> {
                        if(  selectTag != "stop"){
                            BleBackDataRead.readRefreshData(lineChartBX, lineChartBZ, lineChart)
                        }
                        if (UsbContent.connectState) {
                            selectTag = "start"
                            writeData(BleDataMake.makeStartMeterData())
                        }
                    }
                    context.resources.getString(R.string.stop) -> {
                        if (UsbContent.connectState) {
                            selectTag = "stop"
                            writeData(BleDataMake.makeStopMeterData())
                        }
                    }
                    context.resources.getString(R.string.refresh) -> {
                        if (UsbContent.connectState) {
                            selectTag = "refresh"
                            writeData(BleDataMake.makeStopMeterData())
                        }
                        Thread.sleep(500)
                        BleBackDataRead.readRefreshData(lineChartBX, lineChartBZ, lineChart)
                        tbLayout.selectTab(tbLayout.getTabAt(0))
                    }
                    context.resources.getString(R.string.reset) -> {
                        lineChartBX.fitScreen()
                        lineChartBX.invalidate()
                        lineChartBZ.fitScreen()
                        lineChartBX.invalidate()
                        LineChartSetting().mMatrix.let {
                            it.reset()
                        }
                        LineChartSetting().mSavedMatrix.let {
                            it.reset()
                        }
                        if (UsbContent.connectState) {
                            selectTag = "reset"
                            writeData(BleDataMake.makeStopMeterData())
                        }
                    }
                    context.resources.getString(R.string.forms) -> {
                        var viewBX = linBX
                        viewBX.setDrawingCacheEnabled(true)
                        viewBX.buildDrawingCache()
                        var bitmapBX = Bitmap.createBitmap(viewBX.getDrawingCache())

                        var viewBZ = linBZ
                        viewBZ.setDrawingCacheEnabled(true)
                        viewBZ.buildDrawingCache()
                        var bitmapBZ = Bitmap.createBitmap(viewBZ.getDrawingCache())

                        var viewDX = linDX
                        viewDX.setDrawingCacheEnabled(true)
                        viewDX.buildDrawingCache()
                        var bitmapDX = Bitmap.createBitmap(viewDX.getDrawingCache())
                        MainDialog().writeFormDataDialog(
                            this@MainActivity,
                            bitmapBX,
                            bitmapBZ,
                            bitmapDX
                        )
                    }
                    context.resources.getString(R.string.save) -> {
                        mediaManager =
                            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                        if (mMediaProjection == null) {
                            val captureIntent: Intent = mediaManager.createScreenCaptureIntent()
                            startActivityForResult(captureIntent, Constant.TAG_TWO)
                        } else {
                            mMediaProjection?.let {
                                CaptureImage().captureImages(
                                    this@MainActivity,
                                    "image",
                                    it
                                )
                            }
                        }
                    }
                    context.resources.getString(R.string.play_back) -> {
                        if (UsbContent.connectState) {
                            writeData(BleDataMake.makeStopMeterData())
                        }
                        BleBackDataRead.playBack(lineChartBX, lineChartBZ)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageView -> {
                drawer_layout.openDrawer(GravityCompat.START)
            }
            R.id.linImageList -> {
                ImageListActivity.actionStart(this)
            }
            R.id.linFileList -> {
                val path = "%2fandroid%2fdata%2fcom.example.lkacmf%2fcache%2fLKACMFFORM%2f"
                val uri =
                    Uri.parse("content://com.android.externalstorage.documents/document/primary:$path")
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*" //想要展示的文件类型
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
                startActivity(intent)
            }
            R.id.linSetting -> {
                MainDialog().setConfigDialog(this)
            }
            R.id.linVersionCheck -> {
                versionInfo()
            }
            R.id.linContactComp -> {
                BaseTelPhone.telPhone(this)
            }
            R.id.btnFinish -> {
                finish()
            }
        }
    }

    /**
     * 请求版本信息
     */
    private fun versionInfo() {
        val params = HashMap<String, String>()
        params["projectName"] = "济宁鲁科"
        params["actionName"] = "ACMF"
        params["appVersion"] = version
        params["channel"] = "default"
        params["appType"] = "android"
        params["clientType"] = "X2"
        params["phoneSystemVersion"] = "10.0.1"
        params["phoneType"] = "华为"
        val gson = Gson()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            gson.toJson(params)
        )
        versionInfoPresenter.getVersionInfo(requestBody)
    }


    override fun setVersionInfo(versionInfo: VersionInfo?) {
        val netVersion = versionInfo?.data?.version
        val netVersionArray = netVersion?.split(".")?.toTypedArray()
        val localVersionArray = version.split(".").toTypedArray()
        if (netVersionArray != null) {
            for (i in netVersionArray.indices) {
                if (netVersionArray[i].toInt() > localVersionArray[i].toInt()) {
                    if (versionInfo.data.updateFlag === 0) {
                        //无需SSH升级,APK需要升级时值为0
                        showUpDataDialog(versionInfo, 0)
                        return
                    } else if (versionInfo.data.updateFlag === 1) {
                        //SSH需要升级APK不需要升级
                        showUpDataDialog(versionInfo, 1)
                        return
                    } else if (versionInfo.data.updateFlag === 2) {
                        showUpDataDialog(versionInfo, 2)
                        return
                    }
                }
            }
        }
        //        remoteSetting(this.savedInstanceState);
        R.string.last_version.showToast(this)
    }

    private fun showUpDataDialog(versionInfo: VersionInfo, i: Int) {
        val updateInfo: String = versionInfo.data.updateInfo
        val updataItem: Array<String> = updateInfo.split("~").toTypedArray()
        var updateInfo1 = ""
        if (updataItem != null && updataItem.isNotEmpty()) {
            for (j in updataItem.indices) {
                updateInfo1 = "$updateInfo1 \n ${updataItem[j]}"
            }
        }
        UpdateAppUtils
            .getInstance()
            .apkUrl(versionInfo.data.apkUrl)
            .updateConfig(UpdateConfig(alwaysShowDownLoadDialog = true))
            .uiConfig(
                UiConfig(
                    uiType = UiType.CUSTOM,
                    customLayoutId = R.layout.view_update_dialog_custom
                )
            )
            .setOnInitUiListener(object : OnInitUiListener {
                override fun onInitUpdateUi(
                    view: View?,
                    updateConfig: UpdateConfig,
                    uiConfig: UiConfig
                ) {
                    view?.findViewById<TextView>(R.id.tvUpdateTitle)?.text =
                        "${applicationContext.resources.getString(R.string.have_new_version)}${versionInfo.data.version}"
                    view?.findViewById<TextView>(R.id.tvVersionName)?.text =
                        "V${versionInfo.data.version}"
                    view?.findViewById<TextView>(R.id.tvUpdateContent)?.text =
                        updateInfo1
                    // do more...
                }
            })
            .update()
    }

    override fun setVersionInfoMessage(message: String?) {
        message?.let { it.showToast(this) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constant.TAG_ONE -> {
                    mMediaProjection = data?.let { mediaManager.getMediaProjection(resultCode, it) }
                    mMediaProjection?.let { CaptureImage().captureImages(this, "form", it) }
                }
                Constant.TAG_TWO -> {
                    mMediaProjection = data?.let { mediaManager.getMediaProjection(resultCode, it) }
                    mMediaProjection?.let { CaptureImage().captureImages(this, "image", it) }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.e("TAG","XXX")
        if (UsbContent.connectState) {
            selectTag = "reset"
            writeData(BleDataMake.makeStopMeterData())
        }
//        BleContent.releaseBleScanner()
    }
}