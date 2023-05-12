package com.example.lkacmf.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.youth.banner.adapter.BannerAdapter

class ImageAdapter(val datas: ArrayList<Int>?) : BannerAdapter<Int, ImageAdapter.ViewHolder>(datas) {

    //定义ViewHolder
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        }

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val view = LayoutInflater.from(MyApplication.context).inflate(R.layout.adapter_banner_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindView(holder: ViewHolder?, data: Int?, position: Int, size: Int) {
        if (data != null) {
            holder?.imageView?.setImageResource(data)
        }
    }

}