package com.example.lkacmf.data

import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.entity.PopupItem

object MaterialListData {
    fun setMaterialListData(): ArrayList<PopupItem> {
        val materialList = ArrayList<PopupItem>()
        materialList.apply {
            add(PopupItem(MyApplication.context.resources.getString(R.string.material_plate),R.drawable.ic_plate))
            add(PopupItem(MyApplication.context.resources.getString(R.string.material_seam),R.drawable.ic_seam))
            add(PopupItem(MyApplication.context.resources.getString(R.string.material_corner),R.drawable.ic_corner))
        }
        return materialList
    }
}