package com.bulingzhuang.deadline.bean

/**
 * Created by bulingzhuang
 * on 2017/8/25
 * E-mail:bulingzhuang@foxmail.com
 */

class WeatherModel constructor(val results: List<ResultsBean>) {

    data class ResultsBean(val location: LocationBean,
                           val now: NowBean,
                           val last_update: String) {

        data class LocationBean(
                val id: String,
                val name: String,
                val country: String,
                val path: String,
                val timezone: String,
                val timezone_offset: String
        )

        data class NowBean(
                val text: String,
                val code: String,
                val temperature: String,
                var createTime:Long
        )
    }
}
