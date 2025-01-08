package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.Location
import com.liquifysolutions.tms.tmsbackend.repository.LocationRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class LocationService(private val locationRepository: LocationRepository) {
    fun getAllLocations(): List<Location> = locationRepository.getAllLocations()

    fun getLocationById(id: String): Location? = locationRepository.getLocationById(id)

    fun searchLocations(
        search: String?,
        states: List<String>?,
        districts: List<String>?,
        talukas: List<String>?,
        cities: List<String>?,
        statuses: List<String>?,
        getAll: Boolean,
        page: Int?,
        size: Int?
    ): List<Location> {
        return locationRepository.findLocations(search, states, districts, talukas, cities, statuses, page, size, getAll)
    }


    fun createLocation(location: Location): Int  {
        val id = locationRepository.createLocation(location)
        return id
    }

    @Transactional
    fun deactivateLocation(locationId: String) {
        val location = locationRepository.getLocationById(locationId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found with id $locationId")
        val rowsUpdated = locationRepository.deactivateLocation(locationId)
        if(rowsUpdated == 0){
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to deactivate the location")
        }
    }

    @Transactional
    fun activateLocation(locationId: String) {
        val location = locationRepository.getLocationById(locationId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found with id $locationId")
        val rowsUpdated = locationRepository.activateLocation(locationId)
        if(rowsUpdated == 0){
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to activate the location")
        }
    }

    fun updateLocation(location: Location): Int = locationRepository.updateLocation(location)

    fun deleteLocationById(id: String): Int = locationRepository.deleteLocationById(id)
}