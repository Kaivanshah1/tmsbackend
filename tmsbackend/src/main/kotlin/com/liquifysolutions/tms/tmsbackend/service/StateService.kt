package com.liquifysolutions.tms.tmsbackend.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.liquifysolutions.tms.tmsbackend.model.*
import com.liquifysolutions.tms.tmsbackend.model.yaml.*
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class StateService(){

//    fun getAllLocationData(): LocationData {
//        try {
//            val resource = ClassPathResource("data.yml")
//            val file = resource.file
//
//            val locationsWrapper: LocationsWrapper = objectMapper.readValue(file)
//
//            val allData = mutableListOf<Triple<District, List<Taluka>, List<City>>>()
//            val states = mutableListOf<String>()
//
//            for (location in locationsWrapper.locations) {
//                states.add(location.name)
//
//                for (yamlDistrict in location.districts) {
//                    val district = yamlDistrict.toDistrict()
//                    val talukas = mutableListOf<Taluka>()
//                    val cities = mutableListOf<City>()
//
//                    for (yamlTaluka in yamlDistrict.talukas) {
//                        val taluka = yamlTaluka.toTaluka(district.d_id)
//                        talukas.add(taluka)
//
//                        val talukaCities = yamlTaluka.cities.map { cityName ->
//                            cityName.toCity(taluka.t_id)
//                        }
//                        cities.addAll(talukaCities)
//                    }
//
//                    allData.add(Triple(district, talukas, cities))
//                }
//            }
//
//            return LocationData(
//                states = states,
//                districts = allData.map { it.first },
//                talukas = allData.flatMap { it.second },
//                cities = allData.flatMap { it.third }
//            )
//        } catch (e: Exception) {
//            logger.error("Error reading YAML file: ${e.message}", e)
//            throw RuntimeException("Failed to read location data", e)
//        }
//    }
//
//    fun getDistrictsByState(request: StateFilterRequest): List<District> {
//        try {
//            val resource = ClassPathResource("data.yml")
//            val file = resource.file
//            val locationsWrapper: LocationsWrapper = objectMapper.readValue(file)
//
//            // If state is null or empty, return all districts
//            if (request.state.isNullOrBlank()) {
//                return locationsWrapper.locations
//                    .flatMap { location ->
//                        location.districts.map { yamlDistrict ->
//                            yamlDistrict.toDistrict()
//                        }
//                    }
//            }
//
//            // Return districts for specific state
//            return locationsWrapper.locations
//                .find { it.name.equals(request.state, ignoreCase = true) }
//                ?.districts
//                ?.map { yamlDistrict -> yamlDistrict.toDistrict() }
//                ?: emptyList()
//        } catch (e: Exception) {
//            logger.error("Error retrieving districts: ${e.message}", e)
//            throw RuntimeException("Failed to retrieve districts", e)
//        }
//    }
//
//    fun getTalukasByDistrictAndState(request: TalukaFilterRequest, stateFilter: StateFilterRequest): List<Taluka> {
//        val districtsForState = getDistrictsByState(stateFilter)
//        val allData = getAllLocationData()
//
//        return if (request.district != null) {
//            // Find the district ID for the given district name within the filtered state
//            val districtId = districtsForState
//                .find { it.name.equals(request.district, ignoreCase = true) }
//                ?.d_id
//
//            // Filter talukas by district ID
//            districtId?.let { dId ->
//                allData.talukas.filter { it.district_id == dId }
//            } ?: emptyList()
//        } else {
//            // If no district specified, return all talukas for the filtered state's districts
//            val stateDistrictIds = districtsForState.map { it.d_id }
//            allData.talukas.filter { it.district_id in stateDistrictIds }
//        }
//    }
//    fun getAllDistricts(): List<District> = getAllLocationData().districts
//
//    fun getAllTalukas(): List<Taluka> = getAllLocationData().talukas
//
//    fun getAllCities(): List<City> = getAllLocationData().cities
//
//    fun getCitiesByDistrictAndTaluka(request: CityFilterRequest): List<City> {
//        val allData = getAllLocationData()
//
//        // First, find matching district if provided
//        val matchingDistrict = request.district?.let { districtName ->
//            allData.districts.find { it.name.equals(districtName, ignoreCase = true) }
//        }
//
//        // Find matching talukas based on district and taluka name if provided
//        val matchingTalukas = allData.talukas.filter { taluka ->
//            val districtMatches = if (matchingDistrict != null) {
//                taluka.district_id == matchingDistrict.d_id
//            } else true
//
//            val talukaMatches = if (request.taluka != null) {
//                taluka.name.equals(request.taluka, ignoreCase = true)
//            } else true
//
//            districtMatches && talukaMatches
//        }
//
//        // Get the taluka IDs that match our criteria
//        val matchingTalukaIds = matchingTalukas.map { it.t_id }
//
//        // Filter cities based on matching taluka IDs
//        return if (matchingTalukaIds.isNotEmpty()) {
//            allData.cities.filter { city ->
//                city.taluka_id in matchingTalukaIds
//            }
//        } else {
//            emptyList()
//        }
//    }
//
//    fun getCitiesByStateAndDistrictAndTaluka(request: CityFilterRequest): List<City> {
//            val allData = getAllLocationData()
//
//            // 1. Filter by State (if provided)
//            val filteredDistricts = if (!request.state.isNullOrBlank()){
//                getDistrictsByState(StateFilterRequest(request.state)).map {it.d_id}
//            }else {
//                allData.districts.map{it.d_id}
//            }
//
//
//            // 2. Filter by District (if provided)
//            val filteredTalukas = if (!request.district.isNullOrBlank()) {
//                allData.talukas.filter { taluka ->
//                    taluka.district_id in filteredDistricts && taluka.name.equals(request.district, ignoreCase = true)
//                }.map { it.t_id }
//            } else {
//                allData.talukas.filter { it.district_id in filteredDistricts}.map { it.t_id }
//            }
//
//            // 3. Filter by Taluka (if provided)
//            val matchingTalukaIds = if (!request.taluka.isNullOrBlank()) {
//                filteredTalukas.filter { talukaId ->
//                    allData.talukas.find{it.t_id == talukaId}?.name.equals(request.taluka, ignoreCase = true)
//
//                }
//
//            }else{
//                filteredTalukas
//            }
//
//
//            // 4. Finally, filter cities based on the matching taluka IDs
//            return allData.cities.filter { city ->
//                city.taluka_id in matchingTalukaIds
//            }
//        }
}