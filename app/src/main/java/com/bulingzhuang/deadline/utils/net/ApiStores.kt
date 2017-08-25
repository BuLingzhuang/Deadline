package com.bulingzhuang.deadline.utils.net

import com.bulingzhuang.deadline.bean.WeatherModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by bulingzhuang
 * on 2017/8/24
 * E-mail:bulingzhuang@foxmail.com
 */
interface ApiStores {
    companion object {
        val API_NEW_WEATHER_SERVER_URL = "https://api.seniverse.com/"
    }

//    @GET("v3/weather/now.json?key={key}&location={location}&language={language}&unit={unit}")
    @GET("v3/weather/now.json")
    fun loadWeather(@Query("key") key: String = "0dgqeyrbpaxdhejn", @Query("location") location: String = "上海",
                    @Query("language") language: String = "zh-Hans", @Query("unit") unit: String = "c"): Observable<WeatherModel>
}