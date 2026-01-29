package com.izmir.eshot.data.api

import com.izmir.eshot.data.model.ApproachingBusResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API service for İzmir ESHOT endpoints.
 */
interface EshotApiService {
    
    companion object {
        const val BASE_URL_FILES = "https://openfiles.izmir.bel.tr/"
        const val BASE_URL_API = "https://openapi.izmir.bel.tr/"
    }
    
    /**
     * Get all bus stops as CSV.
     * Endpoint: openfiles.izmir.bel.tr/211488/docs/eshot-otobus-duraklari.csv
     */
    @GET("211488/docs/eshot-otobus-duraklari.csv")
    suspend fun getBusStopsCsv(): ResponseBody
    
    /**
     * Get approaching buses for a specific stop.
     * Endpoint: openapi.izmir.bel.tr/api/iztek/duragayaklasanotobusler/{durakId}
     */
    @GET("api/iztek/duragayaklasanotobusler/{durakId}")
    suspend fun getApproachingBuses(
        @Path("durakId") durakId: Int
    ): ApproachingBusResponse
}
