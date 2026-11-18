package com.ip312.labs.gold

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface CbrApi {

    @GET("scripts/xml_metall.asp")
    suspend fun getMetals(
        @Query("date_req1") from: String,
        @Query("date_req2") to: String
    ): ResponseBody
}
