package com.example.lkacmf.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.MyApplication
import com.example.lkacmf.MyApplication.Companion.context
import com.example.lkacmf.R
import com.example.lkacmf.data.CharacteristicUuid
import com.example.lkacmf.network.DownloadApk
import com.example.lkacmf.util.*
import com.example.lkacmf.util.ble.*
import com.example.lkacmf.util.dialog.MainDialog
import com.example.lkacmf.util.linechart.ChartScalyCallBack
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.mediaprojection.CaptureImage
import com.example.lkacmf.util.usb.UsbBackDataLisition
import com.example.lkacmf.util.usb.UsbContent
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_item.view.*
import java.util.*


class MainActivity : BaseActivity(), View.OnClickListener {
    private var version: String = "1.0.0"
    private var isStart: Boolean = false
    private lateinit var mediaManager: MediaProjectionManager
    private var mMediaProjection: MediaProjection? = null
    private var reset:String = "noReset"
    private var i:Int = 0
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
    var axisMaximum:Float = 5.0F
    private lateinit var leftYAxis: YAxis

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

        imageView.setOnClickListener(this)
        linSetting.setOnClickListener(this)
        linImageList.setOnClickListener(this)
        linVersionCheck.setOnClickListener(this)
        linContactComp.setOnClickListener(this)
        btnFinish.setOnClickListener(this)
        ivAdd.setOnClickListener(this)
        ivDown.setOnClickListener(this)
        version = BaseProjectVersion().getPackageInfo(context)?.versionName.toString()
        linCurrentVersion.tvVersion.text = version

        LineChartSetting().SettingLineChart(this, lineChartBX,yAxixSetting,true)
        LineChartSetting().SettingLineChart(this, lineChartBZ, yAxixSetting, true)
        LineChartSetting().SettingLineChart(this, lineChartTest, yAxixSetting, true)
        val xAxis = lineChartBZ.xAxis
        xAxis.textColor = context.resources.getColor(R.color.theme_back_color)


        var permissionTag = MainDialog().requestPermission(this)
        if (permissionTag){
            UsbContent.usbDeviceConstant(this,object : UsbBackDataLisition{
                override fun usbBackData(data: String) {
//                    LogUtil.e("TAG",data)
                    if (data.length > 4 && data.substring(0, 4) == "BE06") {
                        if (BaseData.hexStringToBytes(data.substring(0, data.length - 6)) == data.substring(data.length - 6, data.length-4)&&data.length==38) {
                            BleBackDataRead.readMeterData(data,lineChartBX,lineChartBZ,lineChart)
                        }
                    }
                }

            })
        }else{
            R.string.no_permission.showToast(this)
        }

        //获取电量
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        var receiver = BatteryReceiver()
        registerReceiver(receiver, filter)

//        leftYAxis = lineChartBX.axisLeft
//        leftYAxis.axisMaximum = axisMaximum
    }

    private fun tabLayoutSelect() {
        tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    context.resources.getString(R.string.start) -> {
                    }
                    context.resources.getString(R.string.stop) -> {
                        isStart = false
                        BleContent.writeData(
                            BleDataMake.makeStopMeterData(),
                            CharacteristicUuid.ConstantCharacteristicUuid,
                            object : BleWriteCallBack {
                                override fun writeCallBack(writeBackData: String) {
                                    LogUtil.e("TAG", "写入开始测量回调 = $writeBackData")
                                }
                            })
                    }
                    context.resources.getString(R.string.refresh) -> {
                        reset = "Reset"
                        BleBackDataRead.readRefreshData(lineChartBX)
                        isStart = true
//                        timer.cancel()
                        tbLayout.selectTab(tbLayout.getTabAt(0))
                    }
                    context.resources.getString(R.string.reset) -> {
                        isStart = true
                        reset = "noReset"
                        //缩放第一种方式
                        var matrix = Matrix();
                        //1f代表不缩放
                        matrix.postScale(1f, 1f);
                        lineChartBX.viewPortHandler.refresh(matrix, lineChartBX, false);
                        //重设所有缩放和拖动，使图表完全适合它的边界（完全缩小）。
                        lineChartBX.fitScreen();
                        lineChartBX.invalidate()
                        tbLayout.selectTab(tbLayout.getTabAt(0))
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
                        MainDialog().writeFormDataDialog(this@MainActivity,bitmapBX,bitmapBZ,bitmapDX)
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
                    context.resources.getString(R.string.play_back) -> {
                        BleBackDataRead.playBack(lineChartBX,lineChartBZ)
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
            R.id.linImageList -> {
                ImageListActivity.actionStart(this)
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
            R.id.ivAdd -> {
                axisMaximum+=20
                leftYAxis.axisMaximum = axisMaximum
            }
            R.id.ivDown -> {
                if (axisMaximum>20){
                    axisMaximum-=20
                    leftYAxis.axisMaximum = axisMaximum
                }
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