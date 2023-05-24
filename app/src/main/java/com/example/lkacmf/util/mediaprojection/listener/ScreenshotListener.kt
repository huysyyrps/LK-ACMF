package com.example.lkacmf.util.mediaprojection.listener

import android.graphics.Bitmap

/**
 * 截屏监听
 */
interface ScreenshotListener {

    suspend fun onScreenSuc(bitmap:Bitmap)

}