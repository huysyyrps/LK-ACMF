package com.example.lkacmf.module

import com.example.lkacmf.entity.VersionInfo
import com.example.lkacmf.network.BaseEView
import com.example.lkacmf.network.BasePresenter
import okhttp3.RequestBody

interface VersionInfoContract {
    interface View : BaseEView<presenter?> {
        //获取版本信息
        @Throws(Exception::class)
        fun setVersionInfo(versionInfo: VersionInfo?)
        fun setVersionInfoMessage(message: String?)
    }

    interface presenter : BasePresenter {
        //版本信息回调
        fun getVersionInfo(company: RequestBody?)
    }
}