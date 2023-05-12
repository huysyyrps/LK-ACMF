package com.example.lkacmf.network

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException


class BaseOkHttpClient private constructor() {
    private val okHttpClient: OkHttpClient = OkHttpClient()
    private val handler: Handler = Handler(Looper.getMainLooper())

    internal inner class StringCallBack(request: Request, httpCallBack: HttpCallBack?) :
        Callback {
        private val httpCallBack: HttpCallBack? = httpCallBack
        private val request: Request = request
        override fun onFailure(call: Call, e: IOException) {
            val fe: IOException = e
            if (httpCallBack != null) {
                handler.post(Runnable { httpCallBack.onError(request, fe) })
            }
        }

        override fun onResponse(call: Call, response: Response) {
            val result: String? = response.body?.string()
            if (httpCallBack != null) {
                handler.post(Runnable { httpCallBack.onSuccess(request, result) })
            }
        }

    }

    fun asyncGet(url: String, httpCallBack: HttpCallBack?) {
        val request: Request = Request.Builder().url(url).build()
        okHttpClient.newCall(request).enqueue(StringCallBack(request, httpCallBack))
    }

    fun asyncPost(url: String, httpCallBack: HttpCallBack?) {
        val params = HashMap<String, String>()
        params["projectName"] = "JNLK"
        params["actionName"] = "ACMF"
        params["appVersion"] = "1.0.0"
        params["channel"] = "default"
        params["appType"] = "android"
        params["clientType"] = "ACMF"
        params["phoneSystemVersion"] = "10.0.1"
        params["phoneType"] = "HW"
        val gson = Gson()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            gson.toJson(params)
        )
        val request: Request = Request.Builder().url(url).post(requestBody).build()
        okHttpClient.newCall(request).enqueue(StringCallBack(request, httpCallBack))
    }

    interface HttpCallBack {
        fun onError(request: Request?, e: IOException?)
        fun onSuccess(request: Request?, result: String?)
    }

    companion object {
        private var myOkHttpClient: BaseOkHttpClient? = null
        val instance: BaseOkHttpClient?
            get() {
                if (myOkHttpClient == null) {
                    synchronized(BaseOkHttpClient::class.java) {
                        if (myOkHttpClient == null) {
                            myOkHttpClient = BaseOkHttpClient()
                        }
                    }
                }
                return myOkHttpClient
            }
    }

}