package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.Location
import com.liquifysolutions.tms.tmsbackend.repository.LocationRepository
import org.springframework.stereotype.Service

@Service
class LocationService(private val locationRepository: LocationRepository) {
    fun getAllLocations(): List<Location> = locationRepository.getAllLocations()

    fun getLocationById(id: String): Location? = locationRepository.getLocationById(id)

    fun createLocation(location: Location): Int  {
        val id = locationRepository.createLocation(location)
        return id
    }

    fun updateLocation(location: Location): Int = locationRepository.updateLocation(location)

    fun deleteLocationById(id: String): Int = locationRepository.deleteLocationById(id)
}