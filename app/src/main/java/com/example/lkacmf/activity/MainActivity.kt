package com.example.lkacmf.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.MyApplication
import com.example.lkacmf.MyApplication.Companion.context
import com.example.lkacmf.R
import com.example.lkacmf.data.CharacteristicUuid
import com.example.lkacmf.network.DownloadApk
import com.example.lkacmf.util.*
import com.example.lkacmf.util.dialog.MainDialog
import com.example.lkacmf.util.linechart.LineChartSetting
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_item.view.*
import java.util.*
import kotlin.properties.Delegates


class MainActivity : BaseActivity(), View.OnClickListener {
    //dialog
    private lateinit var dialog: MaterialDialog
    private var version: String = "1.0.0"
    private var isStart: Boolean = false
    private var iswrite: Boolean = false
    private lateinit var lineDataSet: LineDataSet
    private var landList: ArrayList<Entry> = ArrayList()
    private var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
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
    var i = 0

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

        if (!bluetoothAdapter.isEnabled) {
            activityResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        } else {
            MainDialog().requestPermission(this)
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

    //控件属性设置
    private fun initView() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (isStart) {
                    landList.add(Entry(i.toFloat(), ((30..50).random()).toFloat()))
                    lineDataSet = LineDataSet(landList, "A扫")
                    //不绘制数据
                    lineDataSet.setDrawValues(false)
                    //不绘制圆形指示器
                    lineDataSet.setDrawCircles(false)
                    //线模式为圆滑曲线（默认折线）
//                lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                    lineDataSet.setColor(context.resources.getColor(R.color.theme_color))
                    //将数据集添加到数据 ChartData 中
                    val lineData = LineData(lineDataSet)
                    //将数据添加到图表中
                    lineChartBX.data = lineData
                    lineChartBX.notifyDataSetChanged()
                    lineChartBX.invalidate()

                    lineChartBZ.data = lineData
                    lineChartBZ.notifyDataSetChanged()
                    lineChartBZ.invalidate()
                    i++
                }
            }
        }, 0, 300)
    }

    //写入数据
    fun writeHandData() {
        Thread.sleep(1500)
        BleContent.writeData("AE011417050BEA",
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
                        LogUtil.e("TAG", "通知数据获取 = $readData")
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
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    context.resources.getString(R.string.start) -> {
                        isStart = true
                        initView()
                    }
                    context.resources.getString(R.string.stop) -> {
                        isStart = false
                    }
                    context.resources.getString(R.string.refresh) -> {
                        landList.clear()
                        isStart = true
                        initView()
                        tbLayout.selectTab(tbLayout.getTabAt(0))
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
            R.id.linSetting -> {
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