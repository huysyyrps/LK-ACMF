package com.example.lkacmf.activity

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import com.example.lkacmf.util.BaseActivity
import com.example.lkacmf.util.BaseProjectVersion
import com.example.lkacmf.util.BaseTelPhone
import com.example.lkacmf.util.ble.*
import com.example.lkacmf.util.dialog.MainDialog
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.showToast
import com.github.mikephil.charting.data.Entry
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_scan_again.*
import kotlinx.android.synthetic.main.drawer_item.view.*
import org.apache.poi.util.Units
import org.apache.poi.xwpf.usermodel.*
import java.io.*
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

        val dataMap: MutableMap<String, Any> = HashMap()
        dataMap["date"] = "2018年10月14日"
        dataMap["time"] = "08:02"
        dataMap["person"] = "移动开发组"
        dataMap["position"] = "济宁"
        dataMap["device"] = "ACMF"
        dataMap["code"] = "1"
        dataMap["describe"] = "测试秒速"
        dataMap["file"] = "检测文件"
        dataMap["probecode"] = "探头编号"
        dataMap["probefile"] = "探头文件！"

//        dataMap["img"] = Environment.getExternalStorageDirectory().path + "/123.png"
        val templetDocPath = assets.open("acmf.docx")
        writeDocx(templetDocPath, dataMap)
    }


    /**
     * 生成一个docx文件，主要用于直接读取asset目录下的模板文件，不用先复制到sd卡中
     * @param templetDocInStream  模板文件的InputStream
     * @param targetDocPath 生成的目标文件的完整路径
     * @param dataMap 替换的数据
     */
    fun writeDocx(
        templetDocInStream: InputStream,
        dataMap: MutableMap<String, Any>
    ) {
        try {
            //得到模板doc文件的HWPFDocument对象
            val HDocx = XWPFDocument(templetDocInStream)

            val run: XWPFRun = HDocx.createParagraph().createRun()
            val picIn = FileInputStream(File(Environment.getExternalStorageDirectory().path + "/123.png"))
            run.addPicture(
                picIn,
                XWPFDocument.PICTURE_TYPE_PNG,
                "插入图片",
                Units.toEMU(56.0),
                Units.toEMU(56.0)
            )
            picIn.close()


            //替换段落里面的变量
            replaceInPara(HDocx, dataMap)
            //替换表格里面的变量
            replaceInTable(HDocx, dataMap)

            val targetDocPath =
                Environment.getExternalStorageDirectory().path + "/acmf1.docx" //这个目录，不需要申请存储权限
            //写到另一个文件中
            val os: OutputStream = FileOutputStream(targetDocPath)
            //把doc输出到输出流中
            HDocx.write(os)
            os.close()
            templetDocInStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 替换段落里面的变量
     * @param doc 要替换的文档
     * @param params 参数
     */
    private fun replaceInPara(doc: XWPFDocument, params: Map<String, Any>) {
        val iterator = doc.paragraphsIterator
        var para: XWPFParagraph
        while (iterator.hasNext()) {
            para = iterator.next()
            replaceInPara(para, params)
        }
    }


    /**
     * 替换段落里面的变量
     * @param para 要替换的段落
     * @param params 参数
     */
    private fun replaceInPara(para: XWPFParagraph, params: Map<String, Any>) {
        val runs: List<XWPFRun>
        println("para.getParagraphText()==" + para.paragraphText)
        runs = para.runs
        for (i in runs.indices) {
            val run = runs[i]
            var runText = run.toString()
            println("runText==$runText")

            // 替换文本内容，将自定义的$xxx$替换成实际文本
            for ((key, value) in params) {
                runText = runText.replace(key, value.toString() + "")
                //直接调用XWPFRun的setText()方法设置文本时，在底层会重新创建一个XWPFRun，把文本附加在当前文本后面，
                //所以我们不能直接设值，需要先删除当前run,然后再自己手动插入一个新的run。
                para.removeRun(i)
                para.insertNewRun(i).setText(runText)
            }
        }
    }

    /**
     * 替换表格里面的变量
     * @param doc 要替换的文档
     * @param params 参数
     */
    private fun replaceInTable(doc: XWPFDocument, params: Map<String, Any>) {
        val iterator = doc.tablesIterator
        var table: XWPFTable
        var rows: List<XWPFTableRow>
        var cells: List<XWPFTableCell>
        var paras: List<XWPFParagraph>
        while (iterator.hasNext()) {
            table = iterator.next()
            rows = table.rows
            for (row in rows) {
                cells = row.tableCells
                for (cell in cells) {
                    paras = cell.paragraphs
                    for (para in paras) {
                        replaceInPara(para, params)
                    }
                }
            }
        }
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