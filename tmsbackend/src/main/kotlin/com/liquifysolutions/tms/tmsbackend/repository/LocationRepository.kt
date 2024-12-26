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
            l_id = rs.getString("l_id"),
            name = rs.getString("name"),
            point_of_contact = rs.getString("point_of_contact"),
            district = rs.getString("district"),
            taluka = rs.getString("taluka"),
            city = rs.getString("city"),
            email = rs.getString("email"),
            phone = rs.getString("phone"),
            pincode = rs.getString("pincode"),
            address_1 = rs.getString("address_1"),
            address_2 = rs.getString("address_2")
        )
    }

    fun getAllLocations(): List<Location> {
        val sql = "SELECT * FROM Location"
        return jdbcTemplate.query(sql, locationRowMapper)
    }

    fun getLocationById(id: String): Location? {
        val sql = "SELECT * FROM Location WHERE l_id = ?"
        return jdbcTemplate.queryForObject(sql, locationRowMapper, id)
    }

    fun createLocation(location: Location): Int {
        val sql = """
            INSERT INTO Location (l_id, name, point_of_contact, district, taluka, city, email, phone, pincode, address_1, address_2) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        return jdbcTemplate.update(
            sql,
            location.l_id,
            location.name,
            location.point_of_contact,
            location.district,
            location.taluka,
            location.city,
            location.email,
            location.phone,
            location.pincode,
            location.address_1,
            location.address_2
        )
    }

    fun updateLocation(location: Location): Int {
        val sql = """
            UPDATE Location 
            SET name = ?, point_of_contact = ?, district = ?, taluka = ?, city = ?, email = ?, phone = ?, pincode = ?, address_1 = ?, address_2 = ?
            WHERE l_id = ?
        """.trimIndent()
        return jdbcTemplate.update(
            sql,
            location.name,
            location.point_of_contact,
            location.district,
            location.taluka,
            location.city,
            location.email,
            location.phone,
            location.pincode,
            location.address_1,
            location.address_2,
            location.l_id
        )
    }

    fun deleteLocationById(id: String): Int {
        val sql = "DELETE FROM Location WHERE l_id = ?"
        return jdbcTemplate.update(sql, id)
    }
}