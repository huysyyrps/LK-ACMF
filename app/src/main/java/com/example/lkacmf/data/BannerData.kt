package com.example.lkacmf.data

import com.example.lkacmf.R


object BannerData {
    fun setBannerData(): ArrayList<Int> {
        val bannerList = ArrayList<Int>()
        bannerList.apply {
            add(R.drawable.banner1)
            add(R.drawable.banner2)
            add(R.drawable.banner3)
            add(R.drawable.banner4)
            add(R.drawable.banner5)
        }
        return bannerList
    }
}
