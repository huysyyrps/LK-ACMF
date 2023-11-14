package com.example.lkacmf.view

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lkacmf.R
import com.example.lkacmf.adapter.PopupAdapter
import com.example.lkacmf.data.PopupListData
import com.example.lkacmf.data.MaterialListData
import com.example.lkacmf.util.AdapterPositionCallBack
import com.example.lkacmf.util.PopupPositionCallBack
import com.lxj.xpopup.core.BubbleAttachPopupView
import com.lxj.xpopup.util.XPopupUtils

class CustomBubbleAttachPopup : BubbleAttachPopupView {
    var popupPositionCallBack: PopupPositionCallBack
    var selectTag = ""
    lateinit var adapter:PopupAdapter
    constructor(context: Context, tag:String, popupCallBack: PopupPositionCallBack) : super(context) {
        popupPositionCallBack = popupCallBack
        selectTag = tag
    }

    override fun getImplLayoutId(): Int {
        return R.layout.custom_bubble_attach_popup
    }

    override fun onCreate() {
        super.onCreate()
        setBubbleBgColor(R.color.theme_back_color)
        setBubbleShadowSize(XPopupUtils.dp2px(context, 1f))
        //        setBubbleShadowColor(Color.RED);
        setArrowWidth(XPopupUtils.dp2px(context, 8f))
        setArrowHeight(XPopupUtils.dp2px(context, 5f))
        //                                .setBubbleRadius(100)
        setArrowRadius(XPopupUtils.dp2px(context, 2f))
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        if (selectTag=="material"){
            adapter = PopupAdapter(MaterialListData.setMaterialListData(),object : AdapterPositionCallBack {
                override fun backPosition(index: Int) {
                    popupPositionCallBack.backPosition(index)
                    dismiss()
                }
            })
        }else if (selectTag=="popup"){
            adapter = PopupAdapter(PopupListData.setPopupListData(),object : AdapterPositionCallBack {
                override fun backPosition(index: Int) {
                    popupPositionCallBack.backPosition(index)
                    dismiss()
                }
            })
        }
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}