package com.example.lkacmf.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lkacmf.R
import com.example.lkacmf.entity.PopupItem
import com.example.lkacmf.util.AdapterPositionCallBack
 class PopupAdapter(var dataList: ArrayList<PopupItem>, private val adapterPositionCallBack:AdapterPositionCallBack): RecyclerView.Adapter<PopupAdapter.ViewHolder>() {
    //在内部类里面获取到item里面的组件
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val popupItem: LinearLayout = view.findViewById(R.id.popupItem)
        val popupTextView: TextView = view.findViewById(R.id.popupTextView)
        val popupImage: ImageView = view.findViewById(R.id.popupImage)
    }
    //重写的第一个方法，用来给制定加载那个类型的Recycler布局
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.popup_item,parent,false)
        var viewHolder=ViewHolder(view)
        //单机事件
        viewHolder.popupItem.setOnClickListener {
            notifyDataSetChanged()
            adapterPositionCallBack.backPosition(viewHolder.layoutPosition)
        }
        return viewHolder
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.popupTextView.text = dataList[position].title
        holder.popupImage.setImageResource(dataList[position].path)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}