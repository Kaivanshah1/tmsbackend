package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.ListTransportationCompaniesInput
import com.liquifysolutions.tms.tmsbackend.model.TransportationCompany
import com.liquifysolutions.tms.tmsbackend.repository.TransportationCompanyRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class TransportationCompanyService(private val transportationCompanyRepository: TransportationCompanyRepository) {
    fun createCompany(company: TransportationCompany): TransportationCompany {
        val transportationCompanyToCreate = company.copy(
            id = UUID.randomUUID().toString(),
            vehicles = company.vehicles.map { it.copy(id = UUID.randomUUID().toString())},
            drivers = company.drivers.map {it.copy(id = UUID.randomUUID().toString())},
            createdAt = Instant.now().epochSecond,
            updatedAt = Instant.now().epochSecond
        )
        return transportationCompanyRepository.save(transportationCompanyToCreate)
    }

    fun getAllCompanies(request: ListTransportationCompaniesInput): List<TransportationCompany> = transportationCompanyRepository.findAll(request)

    fun getCompanyById(id: String): TransportationCompany? {
        return transportationCompanyRepository.findById(id)
    }

    fun updateCompany(company: TransportationCompany): TransportationCompany {
        if (company.id == null) {
            throw IllegalArgumentException("Transportation company ID cannot be null")
        }

        val transportationCompanyToUpdate = company.copy(
            vehicles = company.vehicles.map { it.copy(id = it.id ?: UUID.randomUUID().toString())},
            drivers = company.drivers.map { it.copy(id = it.id ?: UUID.randomUUID().toString())},
            updatedAt = Instant.now().epochSecond
        )

        return transportationCompanyRepository.update(transportationCompanyToUpdate)

    }
}
