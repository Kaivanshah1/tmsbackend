package com.liquifysolutions.tms.tmsbackend.controller

import com.liquifysolutions.tms.tmsbackend.model.*
import com.liquifysolutions.tms.tmsbackend.service.LocationService
import com.liquifysolutions.tms.tmsbackend.service.StateService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/api")
class LocationController(private val locationService: LocationService, private val stateService: StateService) {
    @GetMapping
    fun getAllLocations(): ResponseEntity<List<Location>> =
        ResponseEntity.ok(locationService.getAllLocations())

    @GetMapping("/{id}")
    fun getLocationById(@PathVariable id: String): ResponseEntity<Location> =
        locationService.getLocationById(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun createLocation(@RequestBody location: Location): ResponseEntity<String> {
        locationService.createLocation(location)
        return ResponseEntity.ok("Location created successfully")
    }

    @PutMapping("/{id}")
    fun updateLocation(@PathVariable id: String, @RequestBody location: Location): ResponseEntity<String> {
        locationService.updateLocation(location.copy(l_id = id))
        return ResponseEntity.ok("Location updated successfully")
    }

    @DeleteMapping("/{id}")
    fun deleteLocationById(@PathVariable id: String): ResponseEntity<String> {
        locationService.deleteLocationById(id)
        return ResponseEntity.ok("Location deleted successfully")
    }

//    @GetMapping("/districts")
//    fun getAllDistricts(): ResponseEntity<List<District>> {
//        return ResponseEntity.ok(stateService.getAllDistricts())
//    }
//
//    @GetMapping("/talukas")
//    fun listTalukas(): ResponseEntity<List<Taluka>> =
//        ResponseEntity.ok(stateService.getAllTalukas())
//
//    @GetMapping("/cities")
//    fun listCities(): ResponseEntity<List<City>> =
//        ResponseEntity.ok(stateService.getAllCities())
//
//    @PostMapping("/talukas/filter")
//    fun filterTalukas(
//        @RequestBody request: TalukaFilterRequest
//    ): ResponseEntity<List<Taluka>> =
//        ResponseEntity.ok(stateService.getTalukasByDistrictAndState(request))
//
//    @PostMapping("/cities/filter")
//    fun filterCities(
//        @RequestBody request: CityFilterRequest
//    ): ResponseEntity<List<City>> =
//        ResponseEntity.ok(stateService.getCitiesByDistrictAndTaluka(request))
}