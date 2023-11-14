package com.example.lkacmf.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import com.example.lkacmf.R
import com.example.lkacmf.util.PopupMenuCallBack
import java.lang.reflect.Field


class BasePopupMenu {
    @SuppressLint("RestrictedApi")
    fun showPopupMenu(view: View?, context: Context, callBack: PopupMenuCallBack) {
        // View当前PopupMenu显示的相对View的位置
        val popupMenu = PopupMenu(context, view)
        // menu布局
//        popupMenu.menuInflater.inflate(R.menu.direction, popupMenu.menu)
        popupMenu.inflate(R.menu.direction);
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener { item ->
            callBack.menuCallBack(item.title as String)
            false
        }
        setForceShowIcon(popupMenu)
        popupMenu.show()
    }

    fun setForceShowIcon(popupMenu: PopupMenu) {
        try {
            val fields: Array<Field> = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper: Any = field.get(popupMenu)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons = classPopupHelper.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}