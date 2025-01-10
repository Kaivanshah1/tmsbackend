package com.liquifysolutions.tms.tmsbackend.controller

import com.liquifysolutions.tms.tmsbackend.model.ListTransportationCompaniesInput
import com.liquifysolutions.tms.tmsbackend.model.TransportationCompany
import com.liquifysolutions.tms.tmsbackend.service.TransportationCompanyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/api/v1/transportation-companies")
class TransportationCompanyController(private val transportationCompanyService: TransportationCompanyService) {
    @PostMapping("/create")
    fun createCompany(@RequestBody company: TransportationCompany): ResponseEntity<TransportationCompany> {
        val created = transportationCompanyService.createCompany(company)
        return ResponseEntity(created, HttpStatus.CREATED)
    }

    @GetMapping("/get/{id}")
    fun getById(@PathVariable id: String): TransportationCompany?{
        return transportationCompanyService.getCompanyById(id);
    }

    @PostMapping("/list")
    fun getAllCompanies(@RequestBody request: ListTransportationCompaniesInput):ResponseEntity<List<TransportationCompany>>{
        val allCompanies = transportationCompanyService.getAllCompanies(request)
        return ResponseEntity(allCompanies,HttpStatus.OK)
    }

    @PostMapping("/update")
    fun updateCompany(@RequestBody company: TransportationCompany): ResponseEntity<TransportationCompany> {
        val updated = transportationCompanyService.updateCompany(company)
        return ResponseEntity(updated, HttpStatus.OK)
    }

//    @PostMapping("/{companyId}")
//    fun createVehicle(@PathVariable companyId: String, @RequestBody vehicle: Vehicles): ResponseEntity<Vehicles> {
//        val created = transportationCompanyService.createVehicle(vehicle, companyId)
//        return ResponseEntity(created, HttpStatus.CREATED)
//    }
//
//    @GetMapping("/{id}")
//    fun getVehicleById(@PathVariable id: String): ResponseEntity<Vehicles> {
//        val vehicle = transportationCompanyService.getVehicleById(id)
//        return ResponseEntity(vehicle, HttpStatus.OK) }
//
//
//    @PostMapping("/{companyId}")
//    fun createDriver(@PathVariable companyId: String, @RequestBody driver: Driver): ResponseEntity<Driver> {
//        val created = transportationCompanyService.createDriver(driver, companyId)
//        return ResponseEntity(created, HttpStatus.CREATED)
//    }
//
//    @GetMapping("/{id}")
//    fun getDriverById(@PathVariable id: String): ResponseEntity<Driver> {
//        val driver = transportationCompanyService.getDriverById(id)
//        return ResponseEntity(driver, HttpStatus.OK)
//    }
//
//    @GetMapping("/company/{companyId}")
//    fun getAllDriversByCompanyId(@PathVariable companyId: String): ResponseEntity<List<Driver>> {
//        val drivers = transportationCompanyService.getAllDriversByCompanyId(companyId)
//        return ResponseEntity(drivers, HttpStatus.OK)
//    }
//
//    @PostMapping("/{id}/{companyId}")
//    fun updateDriver(
//        @PathVariable id: String,
//        @PathVariable companyId: String,
//        @RequestBody driver: Driver
//    ): ResponseEntity<Driver> {
//        val driverToUpdate = driver.copy(id = id)
//        val updated = transportationCompanyService.updateDriver(driverToUpdate, companyId)
//        return ResponseEntity(updated, HttpStatus.OK)
//    }


}
