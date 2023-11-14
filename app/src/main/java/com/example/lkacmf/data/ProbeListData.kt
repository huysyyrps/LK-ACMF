package com.example.lkacmf.data

import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.entity.PopupItem

object ProbeListData {
    fun setProbeListData(): ArrayList<PopupItem> {
        val bannerList = ArrayList<PopupItem>()
        bannerList.apply {
            add(PopupItem(MyApplication.context.resources.getString(R.string.pen_probe),R.drawable.ic_pen_input))
            add(PopupItem(MyApplication.context.resources.getString(R.string.standard_probe),R.drawable.ic_standart))
            add(PopupItem(MyApplication.context.resources.getString(R.string.three_probe),R.drawable.ic_three))
            add(PopupItem(MyApplication.context.resources.getString(R.string.five_probe),R.drawable.ic_five))
            add(PopupItem(MyApplication.context.resources.getString(R.string.seven_probe),R.drawable.ic_seven))
        }
        return bannerList
    }
}