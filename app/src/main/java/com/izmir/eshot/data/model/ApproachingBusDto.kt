package com.izmir.eshot.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object for approaching bus information.
 * Response from: openapi.izmir.bel.tr/api/iztek/duragayaklasanotobusler/{durakId}
 */
@JsonClass(generateAdapter = true)
data class ApproachingBusDto(
    @Json(name = "HatNo")
    val hatNo: String? = null, // Bus route number
    
    @Json(name = "HatAdi")
    val hatAdi: String? = null, // Route name
    
    @Json(name = "Yon")
    val yon: String? = null, // Direction
    
    @Json(name = "KapiNo")
    val kapiNo: String? = null, // Door number (vehicle ID)
    
    @Json(name = "GarajAdi")
    val garajAdi: String? = null, // Garage name
    
    @Json(name = "KalkisSaati")
    val kalkisSaati: String? = null, // Departure time
    
    @Json(name = "Enlem")
    val enlem: Double? = null, // Latitude
    
    @Json(name = "Boylam")
    val boylam: Double? = null, // Longitude
    
    @Json(name = "YaklasmaZamani")
    val yaklasmaZamani: String? = null, // Approaching time/distance
    
    @Json(name = "Hiz")
    val hiz: Int? = null // Speed
)

/**
 * Wrapper response for approaching buses API
 */
@JsonClass(generateAdapter = true)
data class ApproachingBusResponse(
    @Json(name = "HatBilgileri")
    val hatBilgileri: List<ApproachingBusDto>? = null
)
