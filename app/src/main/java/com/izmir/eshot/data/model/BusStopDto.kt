package com.izmir.eshot.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object representing a bus stop from the İzmir ESHOT API.
 * Maps to the CSV columns: DURAK_ID, DURAK_ADI, ENLEM, BOYLAM, HATLAR
 */
@JsonClass(generateAdapter = true)
data class BusStopDto(
    @Json(name = "DURAK_ID")
    val durakId: Int,
    
    @Json(name = "DURAK_ADI")
    val durakAdi: String,
    
    @Json(name = "ENLEM")
    val enlem: Double, // Latitude
    
    @Json(name = "BOYLAM")
    val boylam: Double, // Longitude
    
    @Json(name = "HATLAR")
    val hatlar: String? = null // Bus routes passing through this stop (comma-separated)
)
