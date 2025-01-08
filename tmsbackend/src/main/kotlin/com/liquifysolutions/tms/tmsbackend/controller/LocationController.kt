package com.liquifysolutions.tms.tmsbackend.controller

import com.liquifysolutions.tms.tmsbackend.model.*
import com.liquifysolutions.tms.tmsbackend.service.LocationService
import com.liquifysolutions.tms.tmsbackend.service.StateService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/api/v1/locations")
class LocationController(private val locationService: LocationService, private val stateService: StateService) {

    @PostMapping("/list")
    fun getAllLocations(@RequestBody request: ListLocationsInput): ResponseEntity<List<Location>> {
        val locations = locationService.searchLocations(
            search = request.search,
            states = request.states,
            districts = request.districts,
            talukas = request.talukas,
            cities = request.cities,
            getAll = request.getAll,
            page = request.page,
            size = request.size,
            statuses = request.statuses
        )
        return ResponseEntity.ok(locations)
    }

    @GetMapping("/all")
    fun getAllLocations(): ResponseEntity<List<Location>> {
        val locations = locationService.getAllLocations()
        return ResponseEntity.ok(locations)
    }


    @GetMapping("get/{id}")
    fun getLocationById(@PathVariable id: String): ResponseEntity<Location> {
        return try {
            val location = locationService.getLocationById(id)
            location?.let {
                ResponseEntity.ok(it)
            } ?: ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }

    @GetMapping("/deactivate/{locationId}")
    fun deactivateLocation(@PathVariable locationId: String): ResponseEntity<Void> {
        locationService.deactivateLocation(locationId)
        return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/activate/{locationId}")
    fun activateLocation(@PathVariable locationId: String): ResponseEntity<Void> {
        locationService.activateLocation(locationId)
        return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/create")
    fun createLocation(@RequestBody location: Location): ResponseEntity<String> {
        locationService.createLocation(location)
        return ResponseEntity.ok("Location created successfully")
    }

    @PostMapping("/update")
    fun updateLocation(@RequestBody location: Location): ResponseEntity<String> {
        locationService.updateLocation(location)
        return ResponseEntity.ok("Location updated successfully")
    }

    @DeleteMapping("/{id}")
    fun deleteLocationById(@PathVariable id: String): ResponseEntity<String> {
        locationService.deleteLocationById(id)
        return ResponseEntity.ok("Location deleted successfully")
    }
}