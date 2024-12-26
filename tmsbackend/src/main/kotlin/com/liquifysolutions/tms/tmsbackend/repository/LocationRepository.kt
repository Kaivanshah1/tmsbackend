package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class LocationRepository(private val jdbcTemplate: JdbcTemplate) {

    private val locationRowMapper = RowMapper { rs: ResultSet, _: Int ->
        Location(
            id = rs.getString("id"),
            name = rs.getString("name"),
            contactNumber = rs.getString("contactNumber"),
            addressLine1 = rs.getString("addressLine1"),
            addressLine2 = rs.getString("addressLine2"),
            email = rs.getString("email"),
            pointOfContact = rs.getString("pointOfContact"),
            pincode = rs.getString("pincode"),
            state = rs.getString("state"),
            taluka = rs.getString("taluka"),
            city = rs.getString("city")
        )
    }

    fun getAllLocations(): List<Location> {
        val sql = "SELECT * FROM Location"
        return jdbcTemplate.query(sql, locationRowMapper)
    }

    fun getLocationById(id: String): Location? {
        val sql = "SELECT * FROM Location WHERE id = ?"
        return jdbcTemplate.queryForObject(sql, locationRowMapper, id)
    }

    fun createLocation(location: Location): Int {
        val sql = """
            INSERT INTO Location (id, name, contactNumber, addressLine1, addressLine2, email, pointOfContact, pincode, state, taluka, city) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        return jdbcTemplate.update(
            sql,
            location.id,
            location.name,
            location.contactNumber,
            location.addressLine1,
            location.addressLine2,
            location.email,
            location.pointOfContact,
            location.pincode,
            location.state,
            location.taluka,
            location.city
        )
    }

    fun updateLocation(location: Location): Int {
        val sql = """
            UPDATE Location 
            SET name = ?, contactNumber = ?, addressLine1 = ?, addressLine2 = ?, email = ?, pointOfContact = ?, pincode = ?, state = ?, taluka = ?, city = ?
            WHERE id = ?
        """.trimIndent()
        return jdbcTemplate.update(
            sql,
            location.name,
            location.contactNumber,
            location.addressLine1,
            location.addressLine2,
            location.email,
            location.pointOfContact,
            location.pincode,
            location.state,
            location.taluka,
            location.city,
            location.id
        )
    }

    fun deleteLocationById(id: String): Int {
        val sql = "DELETE FROM Location WHERE id = ?"
        return jdbcTemplate.update(sql, id)
    }
}
