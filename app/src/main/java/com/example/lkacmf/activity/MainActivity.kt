package com.example.lkacmf.activity
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.R
import com.example.lkacmf.data.MaterialListData
import com.example.lkacmf.data.PopupListData
import com.example.lkacmf.entity.VersionInfo
import com.example.lkacmf.module.VersionInfoContract
import com.example.lkacmf.presenter.VersionInfoPresenter
import com.example.lkacmf.util.*
import com.example.lkacmf.util.ble.BaseData
import com.example.lkacmf.util.ble.BleBackDataRead
import com.example.lkacmf.util.ble.BleDataMake
import com.example.lkacmf.util.dialog.MainDialog
import com.example.lkacmf.util.dialog.ThinknessCallBack
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.mediaprojection.CaptureImage
import com.example.lkacmf.util.usb.UsbBackDataLisition
import com.example.lkacmf.util.usb.UsbContent
import com.example.lkacmf.util.usb.UsbContent.writeData
import com.example.lkacmf.view.CustomBubbleAttachPopup
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import constant.UiType
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.drawer_layout
import kotlinx.android.synthetic.main.activity_main.lineChart
import kotlinx.android.synthetic.main.activity_main.lineChartBX
import kotlinx.android.synthetic.main.activity_main.lineChartBZ
import kotlinx.android.synthetic.main.setting.*
import listener.OnInitUiListener
import model.UiConfig
import model.UpdateConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import update.UpdateAppUtils

class MainActivity : BaseActivity(), View.OnClickListener, VersionInfoContract.View {
    private var version: String = "1.0.0"
    private lateinit var versionInfoPresenter: VersionInfoPresenter
    private lateinit var mediaManager: MediaProjectionManager
    private var mMediaProjection: MediaProjection? = null
    private var punctationState:String = ""
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
        mediaManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        versionInfoPresenter = VersionInfoPresenter(this, view = this)
        //tabLayout选择监听
        tabLayoutSelect()

        imageView.setOnClickListener(this)
        linImageList.setOnClickListener(this)
        linFileList.setOnClickListener(this)
        linVersionCheck.setOnClickListener(this)
        linContactComp.setOnClickListener(this)
        btnFinish.setOnClickListener(this)
        btnStart.setOnClickListener(this)
        btnSuspend.setOnClickListener(this)
        btnRefresh.setOnClickListener(this)
        btnPunctation.setOnClickListener(this)
        btnImage.setOnClickListener(this)
        btnDirection.setOnClickListener(this)
        btnThinkness.setOnClickListener(this)
        btnMaterial.setOnClickListener(this)
        vtvSetting.setOnClickListener(this)
        btnBackPlay.setOnClickListener(this)
        btnReset.setOnClickListener(this)
        btnReport.setOnClickListener(this)
        version = ClientVersion.getVersion(applicationContext)
        tvCurrentVersion.text = version

        LineChartSetting().SettingLineChart(this, lineChartBX, true)
        LineChartSetting().SettingLineChart(this, lineChartBZ, true)
        LineChartSetting().SettingMyLineChart(this, lineChart, true)

        val xAxis = lineChartBZ.xAxis
        xAxis.textColor = resources?.getColor(R.color.theme_back_color)!!
        //权限申请
        var permissionTag = MainDialog().requestPermission(this)
        if (permissionTag) {
            UsbContent.usbDeviceConstant(this, object : UsbBackDataLisition {
                override fun usbBackData(data: String) {
                    LogUtil.e("TAG", data)
                    if (data.length > 4 && data.substring(0, 4) == "BE06") {
                        if (BaseData.hexStringToBytes(data.substring(0, data.length - 6)) == data.substring(data.length - 6, data.length - 4) && data.length == 38) {
                            BleBackDataRead.readMeterData(data, lineChartBX, lineChartBZ, lineChart,punctationState)
                            punctationState = ""
                        }
                    }
                    if (data.length > 4 && data.substring(0, 4) == "BE05") {
                        LogUtil.e("TAG", data)
                        if (BaseData.hexStringToBytes(data.substring(0, data.length - 2)) == data.substring(
                                data.length - 2,
                                data.length
                            ) && data.length == 14
                        ) {
                            BleBackDataRead.readSettingData(data)
                        }
                    }
                    if (data.length > 4 && data.substring(0, 4) == "BE16") {
                        LogUtil.e("TAG", data)
//                        if (selectTag=="reset"){
//                            if (UsbContent.connectState) {
//                                writeData(BleDataMake.makeReSetMeterData())
//                                selectTag=""
//                            }
//                        }
                    }
                }
            })
        } else {
            R.string.no_permission.showToast(this)
        }
        if (UsbContent.connectState) {
            UsbContent.writeData(BleDataMake.makeReadSettingData())
        }
    }

    private fun tabLayoutSelect() {
        tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0->{
                        linMain.visibility = VISIBLE
                        linAnalysts.visibility = GONE
                        return
                    }
                    1->{
                        linMain.visibility = GONE
                        linAnalysts.visibility = VISIBLE
                        return
                    }
                    2->{
                        tbLayout.selectTab(tbLayout.getTabAt(0))
                        UsbContent.writeData(BleDataMake.makeStopMeterData())
                        UserInfoActivity.actionStart(this@MainActivity)
                        return
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when(v?.id){
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
            R.id.linVersionCheck -> {
                versionInfo()
            }
            R.id.linContactComp -> {
                BaseTelPhone.telPhone(this)
            }
            R.id.btnFinish -> {
                finish()
            }
            R.id.btnStart -> {
                btnStart.visibility = View.GONE
                btnSuspend.visibility = View.VISIBLE
                UsbContent.writeData(BleDataMake.makeStartMeterData())
            }
            R.id.btnRefresh->{
                if (UsbContent.connectState) {
                    UsbContent.writeData(BleDataMake.makeStopMeterData())
                }
                Thread.sleep(500)
                BleBackDataRead.readRefreshData(lineChartBX, lineChartBZ, lineChart)
                btnStart.visibility = View.VISIBLE
                btnSuspend.visibility = View.GONE
            }
            R.id.btnSuspend -> {
                btnSuspend.visibility = View.GONE
                btnStart.visibility = View.VISIBLE
                UsbContent.writeData(BleDataMake.makeStopMeterData())
            }
            R.id.btnImage -> {
                if (mMediaProjection == null) {
                    val captureIntent: Intent = mediaManager.createScreenCaptureIntent()
                    startActivityForResult(captureIntent, Constant.TAG_ONE)
                } else {
                    mMediaProjection?.let {
                        CaptureImage().captureImages(
                            this,
                            "image",
                            it
                        )
                    }
                }
            }
            //标点
            R.id.btnPunctation->{
                punctationState = "add"
            }
            //路径
            R.id.btnDirection -> {
                com.lxj.xpopup.XPopup.Builder(this)
                    .hasShadowBg(false)
                    .isTouchThrough(true)
                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .atView(btnDirection)
                    .isCenterHorizontal(true)
                    .hasShadowBg(false) // 去掉半透明背景
                    .isClickThrough(true)
                    .asCustom(CustomBubbleAttachPopup(this,"popup", object :PopupPositionCallBack{
                        override fun backPosition(index: Int) {
                            vtv_Direction.text= PopupListData.setPopupListData()[index].title
                        }

                    }))
                    .show()
            }
            //图层
            R.id.btnThinkness -> {
                MainDialog().setThinkness(this,object: ThinknessCallBack {
                    override fun thinknessCallBack(thinkness: String) {
                        vtv_thinkness.text = thinkness
                    }

                })
            }
            //材料
            R.id.btnMaterial -> {
                com.lxj.xpopup.XPopup.Builder(this)
                    .hasShadowBg(false)
                    .isTouchThrough(true)
                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .atView(btnMaterial)
                    .isCenterHorizontal(true)
                    .hasShadowBg(false) // 去掉半透明背景
                    .isClickThrough(true)
                    .asCustom(CustomBubbleAttachPopup(this,"material", object :PopupPositionCallBack{
                        override fun backPosition(index: Int) {
                            vtv_material.text= MaterialListData.setMaterialListData()[index].title
                        }

                    }))
                    .show()
            }
            //探头选择
            R.id.vtvSetting ->{
//                MainDialog().settingDialog(requireActivity())
                MainDialog().setConfigDialog(this)
            }
            //回放
            R.id.btnBackPlay->{
                if (UsbContent.connectState) {
                    writeData(BleDataMake.makeStopMeterData())
                }
                BleBackDataRead.playBack(lineChartBX, lineChartBZ,lineChart)
            }
            //复位
            R.id.btnReset->{
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
                    writeData(BleDataMake.makeStopMeterData())
                }
            }
            //报告
            R.id.btnReport->{
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
                @SuppressLint("SetTextI18n")
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

    @SuppressLint("UseRequireInsteadOfGet")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constant.TAG_ONE -> {
                    mMediaProjection = data?.let { mediaManager.getMediaProjection(resultCode, it) }
                    mMediaProjection?.let { CaptureImage().captureImages(this, "image", it) }
                }
                Constant.TAG_TWO -> {
                    mMediaProjection = data?.let { mediaManager.getMediaProjection(resultCode, it) }
                    mMediaProjection?.let { CaptureImage().captureImages(this, "form", it) }
                }
            }
        }
    }
}