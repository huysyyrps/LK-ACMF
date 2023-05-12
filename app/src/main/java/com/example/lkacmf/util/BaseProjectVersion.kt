package com.example.lkacmf.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

class BaseProjectVersion {
    fun getPackageInfo(context: Context) : PackageInfo? {
        var packageInfo: PackageInfo? = null
        try {
            var packageManager = context.packageManager
            packageInfo = packageManager.getPackageInfo(context.packageName, PackageManager.GET_CONFIGURATIONS)
            packageInfo;
        } catch (e:Exception) {
            e.printStackTrace();
        }
        return packageInfo;
    }
}