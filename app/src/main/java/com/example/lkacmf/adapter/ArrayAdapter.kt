package com.example.lkacmf.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.MyApplication.Companion.context
import com.example.lkacmf.R
import java.io.File

class ArrayAdapter (context: Context, private val dataList: MutableList<String>, private val selectList: MutableList<Int>) : RecyclerView.Adapter<ArrayAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvArrayNum: TextView = view.findViewById(R.id.tvArrayNum)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_array_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var itemData = dataList[position]
        holder.tvArrayNum.text = itemData
        if (selectList.size!=0){
            if (selectList[position]==1){
                holder.tvArrayNum.setBackgroundColor(context.resources.getColor(R.color.theme_back_color))
            }else if (selectList[position]==0){
                holder.tvArrayNum.setBackgroundColor(context.resources.getColor(R.color.theme_color))
            }
            holder.tvArrayNum.setOnClickListener{
                if (selectList[position]==0){
                    selectList[position] = 1
                    holder.tvArrayNum.setBackgroundColor(context.resources.getColor(R.color.theme_back_color))
                }else if (selectList[position]==1){
                    selectList[position] = 0
                    holder.tvArrayNum.setBackgroundColor(context.resources.getColor(R.color.theme_color))
                }
//            if (selectList.contains(position)){
//                selectList.remove(position)
//                holder.tvArrayNum.setBackgroundColor(context.resources.getColor(R.color.theme_color))
//            }else{
//                selectList.add(position)
//                holder.tvArrayNum.setBackgroundColor(context.resources.getColor(R.color.theme_back_color))
//            }
            }
        }
    }

    override fun getItemCount() = dataList.size
}