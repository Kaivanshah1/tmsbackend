package com.liquifysolutions.tms.tmsbackend.controller

import com.liquifysolutions.tms.tmsbackend.model.*
import com.liquifysolutions.tms.tmsbackend.service.StateService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/api/states")
class StateController(private val stateService: StateService) {

    @GetMapping("/all-locations")
    fun getAllLocationData(): ResponseEntity<StateService.LocationData> =
        ResponseEntity.ok(stateService.getAllLocationData())

    @GetMapping("/districts")
    fun getAllDistricts(): ResponseEntity<List<District>> =
        ResponseEntity.ok(stateService.getAllDistricts())

    @PostMapping("/districts/filter")
    fun getDistrictsByState(@RequestBody request: StateFilterRequest): ResponseEntity<List<District>> =
        ResponseEntity.ok(stateService.getDistrictsByState(request))

    @GetMapping("/talukas")
    fun getAllTalukas(): ResponseEntity<List<Taluka>> =
        ResponseEntity.ok(stateService.getAllTalukas())

    @PostMapping("/talukas/filter")
    fun getTalukasByStateAndDistrict(
        @RequestBody request: TalukaFilterRequest
    ): ResponseEntity<List<Taluka>> {
        val stateFilter = StateFilterRequest(request.state)
        return ResponseEntity.ok(stateService.getTalukasByDistrictAndState(request, stateFilter))
    }

    @GetMapping("/cities")
    fun getAllCities(): ResponseEntity<List<City>> =
        ResponseEntity.ok(stateService.getAllCities())

    @PostMapping("/cities/filter")
    fun getCitiesByDistrictAndTaluka(@RequestBody request: CityFilterRequest): ResponseEntity<List<City>> =
        ResponseEntity.ok(stateService.getCitiesByStateAndDistrictAndTaluka(request))
}