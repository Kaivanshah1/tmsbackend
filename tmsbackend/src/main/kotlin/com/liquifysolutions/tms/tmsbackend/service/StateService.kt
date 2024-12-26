package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.config.LocationMappingConfig
import com.liquifysolutions.tms.tmsbackend.model.*
import org.springframework.stereotype.Service

@Service
class StateService(private val locationMappingConfig: LocationMappingConfig){
    fun getAllStates(): List<String> {
        return locationMappingConfig.states.map { it.name }
    }

    fun getDistricts(stateFilterRequest: StateFilterRequest): List<String> {
        return if (stateFilterRequest.states.isNullOrEmpty()) {
            // No states provided; return all districts
            locationMappingConfig.states.flatMap { it.districts }.map { it.name }
        } else {
            locationMappingConfig.states
                .filter { state -> stateFilterRequest.states.any { it.equals(state.name, ignoreCase = true) } }
                .flatMap { it.districts }
                .map { it.name }
        }
    }


    fun getTalukas(talukaFilterRequest: TalukaFilterRequest): List<String> {
        return locationMappingConfig.states
            .filter { state ->
                // Filter by state if provided, otherwise include all states
                talukaFilterRequest.states.isNullOrEmpty() || talukaFilterRequest.states.any { it.equals(state.name, ignoreCase = true) }
            }
            .flatMap { state ->
                state.districts.filter { district ->
                    // Filter by district if provided, otherwise include all districts
                    talukaFilterRequest.districts.isNullOrEmpty() || talukaFilterRequest.districts.any { it.equals(district.name, ignoreCase = true) }
                }
            }
            .flatMap { it.talukas }
            .map { it.name }
    }


    fun getCities(cityFilterRequest: CityFilterRequest): List<String> {
        return locationMappingConfig.states
            .filter { state ->
                // Filter by state if provided, otherwise include all states
                cityFilterRequest.states.isNullOrEmpty() || cityFilterRequest.states.any { it.equals(state.name, ignoreCase = true) }
            }
            .flatMap { state ->
                state.districts.filter { district ->
                    // Filter by district if provided, otherwise include all districts
                    cityFilterRequest.districts.isNullOrEmpty() || cityFilterRequest.districts.any { it.equals(district.name, ignoreCase = true) }

                }
            }
            .flatMap { district ->
                district.talukas.filter { taluka ->
                    // Filter by taluka if provided, otherwise include all talukas
                    cityFilterRequest.talukas.isNullOrEmpty() || cityFilterRequest.talukas.any { it.equals(taluka.name, ignoreCase = true) }

                }
            }
            .flatMap { it.cities }
    }
}