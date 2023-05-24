package com.example.lkacmf.network

import android.view.View
import android.widget.TextView
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.entity.VersionBean
import com.example.lkacmf.util.Constant
import com.example.lkacmf.util.showToast
import com.google.gson.Gson
import constant.UiType
import listener.OnInitUiListener
import model.UiConfig
import model.UpdateConfig
import okhttp3.Request
import update.UpdateAppUtils
import java.io.IOException

class DownloadApk {
    fun downloadApk(mainActivity: MainActivity, version: String) {
        BaseOkHttpClient.instance
            ?.asyncPost("${Constant.api}", object : BaseOkHttpClient.HttpCallBack {
                override fun onError(request: Request?, e: IOException?) {
                    R.string.request_faile.showToast(mainActivity)
                }

                override fun onSuccess(request: Request?, result: String?) {
                    var backData = Gson().fromJson(result, VersionBean::class.java)
                    val netVersion: String = backData.data.version
                    val netVersionArray = netVersion.split(".")
                    val localVersionArray = version.split(".")
                    for (i in netVersionArray.indices) {
                        if (netVersionArray[i].toInt() > localVersionArray[i].toInt()) {
                            //无需SSH升级,APK需要升级时值为0
//                                    UpdateDialog(this@MainActivity,backData.data.updateInfo).show()
                            UpdateAppUtils
                                .getInstance()
                                .apkUrl(backData.data.apkUrl)
                                .updateTitle("发现新版本V${backData.data.version}")
                                .updateContent(backData.data.updateInfo)
                                .updateConfig(UpdateConfig(alwaysShowDownLoadDialog = true))
                                .uiConfig(UiConfig(uiType = UiType.CUSTOM, customLayoutId = R.layout.view_update_dialog_custom))
                                .setOnInitUiListener(object : OnInitUiListener {
                                    override fun onInitUpdateUi(view: View?, updateConfig: UpdateConfig, uiConfig: UiConfig) {
                                        view?.findViewById<TextView>(R.id.tv_update_title)?.text = "版本更新啦"
                                        view?.findViewById<TextView>(R.id.tv_version_name)?.text = "V2.0.0"
                                        // do more...
                                    }
                                })
                                .update()

                            return
                        }
                    }
                    R.string.last_version.showToast(mainActivity)
                }
            })
    }
}