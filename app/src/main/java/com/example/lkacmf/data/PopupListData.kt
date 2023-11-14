package com.example.lkacmf.data

import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.entity.PopupItem

object PopupListData {
    fun setPopupListData(): ArrayList<PopupItem> {
        val bannerList = ArrayList<PopupItem>()
        bannerList.apply {
            add(PopupItem(MyApplication.context.resources.getString(R.string.auto),R.drawable.ic_auto))
            add(PopupItem(MyApplication.context.resources.getString(R.string.T),R.drawable.ic_t))
            add(PopupItem(MyApplication.context.resources.getString(R.string.L),R.drawable.ic_l))
            add(PopupItem(MyApplication.context.resources.getString(R.string.R),R.drawable.ic_r))
            add(PopupItem(MyApplication.context.resources.getString(R.string.C),R.drawable.ic_center))
            add(PopupItem(MyApplication.context.resources.getString(R.string.Z),R.drawable.ic_z))
        }
        return bannerList
    }
}