package com.bulingzhuang.deadline.utils.net

import com.bulingzhuang.deadline.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by bulingzhuang
 * on 2017/8/24
 * E-mail:bulingzhuang@foxmail.com
 */
object ApiClient {
    fun retrofit(): ApiStores {
        val okHttpBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            //设置Log信息拦截器
        }
        val okHttpClient = okHttpBuilder.build()
        val retrofitBuilder = Retrofit.Builder()
        val retrofitClient = retrofitBuilder.baseUrl(ApiStores.API_NEW_WEATHER_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient).build()
        return retrofitClient.create(ApiStores::class.java)
    }
}