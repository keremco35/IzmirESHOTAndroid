package com.izmir.eshot.data.repository

import com.izmir.eshot.data.api.EshotApiService
import com.izmir.eshot.domain.model.ApproachingBus
import com.izmir.eshot.domain.model.BusStop
import com.izmir.eshot.domain.repository.BusStopRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

/**
 * Implementation of BusStopRepository.
 * Handles CSV parsing and API calls for bus data.
 */
class BusStopRepositoryImpl @Inject constructor(
    @Named("FilesApiService") private val filesApiService: EshotApiService,
    @Named("ApiService") private val apiService: EshotApiService
) : BusStopRepository {
    
    override suspend fun getBusStops(): List<BusStop> = withContext(Dispatchers.IO) {
        val response = filesApiService.getBusStopsCsv()
        val csvContent = response.string()
        parseCsvToBusStops(csvContent)
    }
    
    override suspend fun getApproachingBuses(stopId: Int): List<ApproachingBus> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getApproachingBuses(stopId)
            response.hatBilgileri?.mapNotNull { dto ->
                try {
                    ApproachingBus(
                        routeNumber = dto.hatNo ?: "",
                        routeName = dto.hatAdi ?: "",
                        direction = dto.yon ?: "",
                        vehicleId = dto.kapiNo ?: "",
                        departureTime = dto.kalkisSaati ?: "",
                        approachingTime = dto.yaklasmaZamani ?: "",
                        latitude = dto.enlem,
                        longitude = dto.boylam,
                        speed = dto.hiz
                    )
                } catch (e: Exception) {
                    // Skip malformed bus data
                    null
                }
            } ?: emptyList()
        } catch (e: Exception) {
            // Return empty list on any API error
            emptyList()
        }
    }
    
    /**
     * Parses CSV content into BusStop domain objects.
     * CSV format: DURAK_ID;DURAK_ADI;ENLEM;BOYLAM;HATLAR
     */
    private fun parseCsvToBusStops(csvContent: String): List<BusStop> {
        val lines = csvContent.lines()
        if (lines.isEmpty()) return emptyList()
        
        // Skip header row and parse data rows
        return lines.drop(1)
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                try {
                    // CSV uses semicolon as delimiter
                    val parts = line.split(";")
                    if (parts.size >= 4) {
                        val id = parts[0].trim().toIntOrNull() ?: return@mapNotNull null
                        val name = parts[1].trim()
                        val lat = parts[2].trim().replace(",", ".").toDoubleOrNull() ?: return@mapNotNull null
                        val lng = parts[3].trim().replace(",", ".").toDoubleOrNull() ?: return@mapNotNull null
                        val routes = if (parts.size > 4 && parts[4].isNotBlank()) {
                            parts[4].split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        } else {
                            emptyList()
                        }
                        
                        BusStop(
                            id = id,
                            name = name,
                            latitude = lat,
                            longitude = lng,
                            routes = routes
                        )
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null // Skip malformed lines
                }
            }
    }
}
