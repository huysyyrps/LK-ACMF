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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.lkacmf.R
import com.example.lkacmf.entity.VersionInfo
import com.example.lkacmf.fragment.AnalystsFragment
import com.example.lkacmf.fragment.HomeFragment
import com.example.lkacmf.fragment.UserInfoFragment
import com.example.lkacmf.module.VersionInfoContract
import com.example.lkacmf.presenter.VersionInfoPresenter
import com.example.lkacmf.util.*
import com.example.lkacmf.util.dialog.MainDialog
import com.example.lkacmf.util.mediaprojection.CaptureImage
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
    private lateinit var mFm: FragmentManager
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTagList = arrayOf("OneFragment", "TwoFragment", "ThreeFragment")
    private lateinit var mCurrentFragmen: Fragment  // 记录当前显示的Fragment
    private var version: String = "1.0.0"
    private lateinit var versionInfoPresenter: VersionInfoPresenter

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
        mFragmentList.add(0, HomeFragment())
        mFragmentList.add(1, AnalystsFragment())
        mFragmentList.add(2, UserInfoFragment())
        mCurrentFragmen = mFragmentList[0]
        // 初始化首次进入时的Fragment
        mFm = supportFragmentManager;
        val transaction: FragmentTransaction = mFm.beginTransaction()
        transaction.add(R.id.mainFrameLayout, mCurrentFragmen, mFragmentTagList[0])
        transaction.commitAllowingStateLoss()
        versionInfoPresenter = VersionInfoPresenter(this, view = this)
        //tabLayout选择监听
        tabLayoutSelect()

        imageView.setOnClickListener(this)
        linImageList.setOnClickListener(this)
        linFileList.setOnClickListener(this)
        linVersionCheck.setOnClickListener(this)
        linContactComp.setOnClickListener(this)
        btnFinish.setOnClickListener(this)
        version = ClientVersion.getVersion(applicationContext)
        tvCurrentVersion.text = version
        //权限申请
        MainDialog().requestPermission(this)

    }

    private fun tabLayoutSelect() {
        tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0->{
                        switchFragment(mFragmentList[0], mFragmentTagList[0])
                        return
                    }
                    1->{
                        switchFragment(mFragmentList[1], mFragmentTagList[1])
                        return
                    }
                    2->{
                        switchFragment(mFragmentList[2], mFragmentTagList[2])
                        return
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

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

    // 转换Fragment
    fun switchFragment(to: Fragment, tag: String?) {
        if (mCurrentFragmen !== to) {
            val transaction = mFm.beginTransaction()
            if (!to.isAdded) {
                // 没有添加过:
                // 隐藏当前的，添加新的，显示新的
                transaction.hide(mCurrentFragmen).add(R.id.mainFrameLayout, to, tag).show(to)
            } else {
                // 隐藏当前的，显示新的
                transaction.hide(mCurrentFragmen).show(to)
            }
            mCurrentFragmen = to
            transaction.commitAllowingStateLoss()
        }
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
}