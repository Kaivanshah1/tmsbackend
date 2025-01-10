package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Locale

@Repository
class TransportationCompanyRepository(private val jdbcTemplate: JdbcTemplate){

    val transportationCompanyRowMapper = RowMapper<TransportationCompany> { rs, _ ->
        TransportationCompany(
            id = rs.getString("id"),
            companyName = rs.getString("companyname"),
            pointOfContact = rs.getString("pointofcontact"),
            contactNumber = rs.getString("contactnumber"),
            email = rs.getString("email"),
            addressLine1 = rs.getString("addressline1"),
            addressLine2 = rs.getString("addressline2"),
            state = rs.getString("state"),
            city = rs.getString("city"),
            pinCode = rs.getString("pincode"),
            status = rs.getString("status"),
            createdAt = rs.getLong("createdat"),
            updatedAt = rs.getLong("updatedat")
        )
    }

    val vehicleRowMapper = RowMapper<Vehicles> { rs, _ ->
        Vehicles(
            id = rs.getString("id"),
            vehicleNumber = rs.getString("vehiclenumber"),
            type = rs.getString("type"),
            rcBookUrl = rs.getString("rcbookurl"),
            createdAt = rs.getLong("createdat"),
            updatedAt = rs.getLong("updatedat")
        )
    }

    val driverRowMapper = RowMapper<Driver> { rs, _ ->
        Driver(
            id = rs.getString("id"),
            name = rs.getString("name"),
            contactNumber = rs.getString("contactnumber"),
            drivingLicenseUrl = rs.getString("drivinglicenseurl"),
            createdAt = rs.getLong("createdat"),
            updatedAt = rs.getLong("updatedat")
        )
    }

    fun save(company: TransportationCompany): TransportationCompany {
        val sql = "INSERT INTO TransportationCompany (id, companyname, pointofcontact, contactnumber, email, addressline1, addressline2, state, city, pincode, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

        jdbcTemplate.update(sql,
            company.id,
            company.companyName,
            company.pointOfContact,
            company.contactNumber,
            company.email,
            company.addressLine1,
            company.addressLine2,
            company.state,
            company.city,
            company.pinCode,
            company.status
        )

        company.vehicles.forEach{ vehicle -> createVehicle(vehicle, company.id!!) }
        company.drivers.forEach { driver -> createDriver(driver, company.id!!)}

        return company
    }

    fun findById(id: String): TransportationCompany? {
        try {
            val sql = "select * from transportationcompany where id = ?"
            val transportationCompany = jdbcTemplate.queryForObject(sql, transportationCompanyRowMapper, id)

            val driver = findAllDriverByCompanyId(id);
            val vehicles = findVechicleByCompanyId(id);

            return transportationCompany?.copy(
                drivers = driver,
                vehicles = vehicles
            )
        }catch (e: Exception){
            throw e;
        }
    }

    fun findAll(request: ListTransportationCompaniesInput): List<TransportationCompany> {
        try {
            val sqlBuilder = StringBuilder("SELECT * FROM TransportationCompany WHERE 1=1")
            val params = mutableListOf<Any>()

            // 1. Search - Fixed the LIKE condition
            if (request.search.isNotBlank()) {
                val searchTerm = "%${request.search.trim().lowercase(Locale.getDefault())}%"
                sqlBuilder.append(" AND ILIKE ? ")  // Added LOWER for case-insensitive search
                params.add(searchTerm)
            }

            // 2. Status Filtering - Ensure proper handling of statuses
            if (request.statuses.isNotEmpty()) {
                sqlBuilder.append(" AND status IN (")
                val statusPlaceholders = List(request.statuses.size) { "?" }.joinToString(",")
                sqlBuilder.append(statusPlaceholders)
                sqlBuilder.append(")")
                params.addAll(request.statuses)
            }

            // 3. Pagination
            if (!request.getAll) {
                val offset = (request.page - 1) * request.size
                sqlBuilder.append(" ORDER BY companyname ") // Added ordering for consistency
                sqlBuilder.append(" LIMIT ? OFFSET ? ")
                params.add(request.size)
                params.add(offset)
            }

            // Execute Query
            val sql = sqlBuilder.toString()
            println("Generated SQL: $sql")
            println("Parameters: $params")
            val companies = jdbcTemplate.query(sql, params.toTypedArray(), transportationCompanyRowMapper)
            return companies.mapNotNull { company -> findById(company.id!!) }
        }catch(e: Exception){
            throw e;
        }
    }

    fun update(company: TransportationCompany): TransportationCompany {
        val sql = "UPDATE TransportationCompany SET companyname = ?, pointofcontact = ?, contactnumber = ?, email = ?, addressline1 = ?, addressline2 = ?, state = ?, city = ?, pincode = ?, status = ? WHERE id = ?"
        jdbcTemplate.update(sql,
            company.companyName,
            company.pointOfContact,
            company.contactNumber,
            company.email,
            company.addressLine1,
            company.addressLine2,
            company.state,
            company.city,
            company.pinCode,
            company.status,
            company.id
        )

        // 2. Get existing vehicles and drivers from database
        val existingVehicles = findVechicleByCompanyId(company.id!!)
        val existingDrivers = findAllDriverByCompanyId(company.id)

        // 3. Get new vehicles and drivers from the updated transportation company
        val newVehicles = company.vehicles
        val newDrivers = company.drivers

        // 4. Categorize vehicles
        val existingVehicleIds = existingVehicles.mapNotNull { it.id }.toSet()
        val newVehicleIds = newVehicles.mapNotNull { it.id }.toSet()

        val vehiclesToCreate = newVehicles.filter { it.id == null || !existingVehicleIds.contains(it.id) }
        val vehiclesToUpdate = newVehicles.filter { it.id != null && existingVehicleIds.contains(it.id) }
        val vehiclesToDelete = existingVehicles.filter { it.id != null && !newVehicleIds.contains(it.id) }

        // 5. Delete removed vehicles
        if (vehiclesToDelete.isNotEmpty()) {
            val deleteVehicleSql = """
                    DELETE FROM vehicles
                    WHERE id = ?
                """.trimIndent()
            vehiclesToDelete.forEach { vehicle ->
                jdbcTemplate.update(deleteVehicleSql, vehicle.id)
            }
        }

        // 6. Update existing vehicles
        val updateVehicleSql = """
                UPDATE vehicles
                SET
                    vehicle_number = ?,
                    rc_book_url = ?,
                    status = ?
                WHERE id = ?
            """.trimIndent()

        vehiclesToUpdate.forEach { vehicle ->
            jdbcTemplate.update(
                updateVehicleSql,
                vehicle.vehicleNumber,
                vehicle.rcBookUrl,
                vehicle.id
            )
        }

        // 7. Create new vehicles
        vehiclesToCreate.forEach { vehicle -> createVehicle(vehicle, company.id) }

        // 8. Categorize drivers
        val existingDriverIds = existingDrivers.mapNotNull { it.id }.toSet()
        val newDriverIds = newDrivers.mapNotNull { it.id }.toSet()

        val driversToCreate = newDrivers.filter { it.id == null || !existingDriverIds.contains(it.id) }
        val driversToUpdate = newDrivers.filter { it.id != null && existingDriverIds.contains(it.id) }
        val driversToDelete = existingDrivers.filter { it.id != null && !newDriverIds.contains(it.id) }

        // 9. Delete removed drivers
        if (driversToDelete.isNotEmpty()) {
            val deleteDriverSql = """
                    DELETE FROM drivers
                    WHERE id = ?
                """.trimIndent()
            driversToDelete.forEach { driver ->
                jdbcTemplate.update(deleteDriverSql, driver.id)
            }
        }

        // 10. Update existing drivers
        val updateDriverSql = """
                UPDATE drivers
                SET
                    name = ?,
                    contact_number = ?,
                    driving_license_url = ?,
                    status = ?
                WHERE id = ?
            """.trimIndent()

        driversToUpdate.forEach { driver ->
            jdbcTemplate.update(
                updateDriverSql,
                driver.name,
                driver.contactNumber,
                driver.drivingLicenseUrl,
                driver.id
            )
        }

        // 11. Create new drivers
        driversToCreate.forEach { driver ->createDriver(driver, company.id)}

        return company
    }

    @Transactional
    fun createVehicle(vehicle: Vehicles, companyId: String): Vehicles {
        val sql = "INSERT INTO Vehicles (id, vehiclenumber, type, rcbookurl, companyid) VALUES (?, ?, ?, ?, ?)"
        jdbcTemplate.update(
            sql,
            vehicle.id,
            vehicle.vehicleNumber,
            vehicle.type,
            vehicle.rcBookUrl,
            companyId
        )
        return vehicle
    }

    fun findVechicleByCompanyId(companyId:String) : List<Vehicles>{
        val sql = "SELECT * FROM Vehicles WHERE companyid = ?"
        return jdbcTemplate.query(sql, vehicleRowMapper,companyId)
    }

    @Transactional
    fun createDriver(driver: Driver, companyId: String): Driver {
        val sql = "INSERT INTO Driver (id, name, contactnumber, drivinglicenseurl, companyid) VALUES (?, ?, ?, ?, ?)"
        jdbcTemplate.update(
            sql,
            driver.id,
            driver.name,
            driver.contactNumber,
            driver.drivingLicenseUrl,
            companyId
        )
        return driver
    }

    fun findAllDriverByCompanyId(companyId: String) : List<Driver>{
        val sql = "SELECT * FROM Driver WHERE companyid = ?"
        return jdbcTemplate.query(sql, driverRowMapper,companyId)
    }
}