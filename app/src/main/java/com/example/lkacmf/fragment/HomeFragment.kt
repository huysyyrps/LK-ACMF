package com.example.lkacmf.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context.MEDIA_PROJECTION_SERVICE
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.data.PopupListData
import com.example.lkacmf.data.MaterialListData
import com.example.lkacmf.util.Constant
import com.example.lkacmf.util.PopupPositionCallBack
import com.example.lkacmf.util.ble.BaseData
import com.example.lkacmf.util.ble.BleBackDataRead
import com.example.lkacmf.util.ble.BleDataMake
import com.example.lkacmf.util.dialog.MainDialog
import com.example.lkacmf.util.dialog.ThinknessCallBack
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.mediaprojection.CaptureImage
import com.example.lkacmf.util.showToast
import com.example.lkacmf.util.usb.UsbBackDataLisition
import com.example.lkacmf.util.usb.UsbContent
import com.example.lkacmf.util.usb.UsbContent.writeData
import com.example.lkacmf.view.CustomBubbleAttachPopup
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), View.OnClickListener {
    private lateinit var mediaManager: MediaProjectionManager
    private var mMediaProjection: MediaProjection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mediaManager = activity?.getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStart() {
        super.onStart()
        btnStart.setOnClickListener(this)
        btnSuspend.setOnClickListener(this)
        btnRefresh.setOnClickListener(this)
        btnPunctation.setOnClickListener(this)
        btnImage.setOnClickListener(this)
        btnDirection.setOnClickListener(this)
        btnThinkness.setOnClickListener(this)
        btnMaterial.setOnClickListener(this)
        vtvSetting.setOnClickListener(this)

        LineChartSetting().SettingLineChart(this, lineChartBX, true)
        LineChartSetting().SettingLineChart(this, lineChartBZ, true)
        LineChartSetting().SettingMyLineChart(this, lineChart, true)
        val xAxis = lineChartBZ.xAxis
        xAxis.textColor = context?.resources?.getColor(R.color.theme_back_color)!!
        var permissionTag = MainDialog().requestPermission(requireActivity() as MainActivity)
        if (permissionTag) {
            UsbContent.usbDeviceConstant(requireActivity(), object : UsbBackDataLisition {
                override fun usbBackData(data: String) {
                    LogUtil.e("TAG", data)
                    if (data.length > 4 && data.substring(0, 4) == "BE06") {
                        if (BaseData.hexStringToBytes(data.substring(0, data.length - 6)) == data.substring(
                                data.length - 6,
                                data.length - 4
                            ) && data.length == 38
                        ) {
                            BleBackDataRead.readMeterData(data, lineChartBX, lineChartBZ, lineChart)
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
            activity?.let { R.string.no_permission.showToast(it) }
        }
        if (UsbContent.connectState) {
            writeData(BleDataMake.makeReadSettingData())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnStart -> {
                btnStart.visibility = GONE
                btnSuspend.visibility = VISIBLE
                writeData(BleDataMake.makeStartMeterData())
            }
            R.id.btnRefresh->{
                if (UsbContent.connectState) {
                    writeData(BleDataMake.makeStopMeterData())
                }
                Thread.sleep(500)
                BleBackDataRead.readRefreshData(lineChartBX, lineChartBZ, lineChart)
            }
            R.id.btnSuspend -> {
                btnSuspend.visibility = GONE
                btnStart.visibility = VISIBLE
                writeData(BleDataMake.makeStopMeterData())
            }
            R.id.btnImage -> {
                if (mMediaProjection == null) {
                    val captureIntent: Intent = mediaManager.createScreenCaptureIntent()
                    startActivityForResult(captureIntent, Constant.TAG_ONE)
                } else {
                    mMediaProjection?.let {
                        CaptureImage().captureImages(
                            requireActivity(),
                            "image",
                            it
                        )
                    }
                }
            }
            R.id.btnDirection -> {
                com.lxj.xpopup.XPopup.Builder(context)
                    .hasShadowBg(false)
                    .isTouchThrough(true)
                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .atView(btnDirection)
                    .isCenterHorizontal(true)
                    .hasShadowBg(false) // 去掉半透明背景
                    .isClickThrough(true)
                    .asCustom(context?.let { CustomBubbleAttachPopup(it,"popup", object :PopupPositionCallBack{
                        override fun backPosition(index: Int) {
                            vtv_Direction.text=PopupListData.setPopupListData()[index].title
                        }

                    }) })
                    .show()
            }
            R.id.btnThinkness -> {
                MainDialog().setThinkness(requireActivity(),object:ThinknessCallBack{
                    override fun thinknessCallBack(thinkness: String) {
                        vtv_thinkness.text = thinkness
                    }

                })
            }
            R.id.btnMaterial -> {
                com.lxj.xpopup.XPopup.Builder(context)
                    .hasShadowBg(false)
                    .isTouchThrough(true)
                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .atView(btnMaterial)
                    .isCenterHorizontal(true)
                    .hasShadowBg(false) // 去掉半透明背景
                    .isClickThrough(true)
                    .asCustom(context?.let { CustomBubbleAttachPopup(it,"material", object :PopupPositionCallBack{
                        override fun backPosition(index: Int) {
                            vtv_material.text=MaterialListData.setMaterialListData()[index].title
                        }

                    }) })
                    .show()
            }
            R.id.vtvSetting ->{
//                MainDialog().settingDialog(requireActivity())
                MainDialog().setConfigDialog(requireActivity())
            }
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constant.TAG_ONE -> {
                    mMediaProjection = data?.let { mediaManager.getMediaProjection(resultCode, it) }
                    mMediaProjection?.let { CaptureImage().captureImages(activity!!, "image", it) }
                }
                Constant.TAG_TWO -> {
                    mMediaProjection = data?.let { mediaManager.getMediaProjection(resultCode, it) }
                    mMediaProjection?.let { CaptureImage().captureImages(activity!!, "form", it) }
                }
            }
        }
    }
}