package com.liquifysolutions.tms.tmsbackend.controller

import com.liquifysolutions.tms.tmsbackend.model.*
import com.liquifysolutions.tms.tmsbackend.service.StateService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/api/v1")
class StateController(private val stateService: StateService) {
    @GetMapping("/states/list")
    fun getAllState(): ResponseEntity<List<String>>{
        return ResponseEntity.ok(stateService.getAllStates());
    }

    @PostMapping("/districts/list")
    fun getDistrictsByState(@RequestBody request: StateFilterRequest): ResponseEntity<List<String>> =
        ResponseEntity.ok(stateService.getDistricts(request))

    @PostMapping("/talukas/list")
    fun getTalukasByStateAndDistrict(
        @RequestBody request: TalukaFilterRequest
    ): ResponseEntity<List<String>> {
        return ResponseEntity.ok(stateService.getTalukas(request))
    }

    @PostMapping("/cities/list")
    fun getCitiesByDistrictAndTaluka(@RequestBody request: CityFilterRequest): ResponseEntity<List<String>> =
        ResponseEntity.ok(stateService.getCities(request))
}