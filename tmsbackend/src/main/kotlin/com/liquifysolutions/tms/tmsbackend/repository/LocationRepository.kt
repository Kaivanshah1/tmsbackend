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
            district = rs.getString("district"),
            pincode = rs.getString("pincode"),
            state = rs.getString("state"),
            taluka = rs.getString("taluka"),
            city = rs.getString("city"),
            status = rs.getString("status"),
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

    fun findLocations(
        search: String?,
        states: List<String>?,
        districts: List<String>?,
        talukas: List<String>?,
        cities: List<String>?,
        statuses: List<String>?,
        page: Int?,
        size: Int?,
        getAll: Boolean? = false
    ): List<Location> {
        val whereClauses = mutableListOf<String>()
        val params = mutableListOf<Any>()

        if (!search.isNullOrBlank()) {
            whereClauses.add("name ILIKE ?")
            params.add("%${search}%")
        }

        if (!states.isNullOrEmpty()) {
            whereClauses.add("state IN (${states.joinToString { "?" }})")
            params.addAll(states)
        }

        if (!districts.isNullOrEmpty()) {
            whereClauses.add("district IN (${districts.joinToString { "?" }})")
            params.addAll(districts)
        }

        if (!talukas.isNullOrEmpty()) {
            whereClauses.add("taluka IN (${talukas.joinToString { "?" }})")
            params.addAll(talukas)
        }
        if (!cities.isNullOrEmpty()) {
            whereClauses.add("city IN (${cities.joinToString { "?" }})")
            params.addAll(cities)
        }
        if (!statuses.isNullOrEmpty()) {
            whereClauses.add("status IN (${statuses.joinToString { "?" }})")
            params.addAll(statuses)
        }

        val whereClause = if (whereClauses.isNotEmpty()) "WHERE " + whereClauses.joinToString(" AND ") else ""

        var sql = "SELECT id, name, contactnumber, addressline1, addressline2, email, pointofcontact, pincode, state, district, taluka, city, status FROM location $whereClause ORDER BY createdat DESC"
        if(getAll == true){
            return jdbcTemplate.query(sql, locationRowMapper , *params.toTypedArray())
        }
        if(page != null && size != null){
            sql += " LIMIT ? OFFSET ?"
            params.add(size)
            params.add((page - 1) * size)
        }

        return jdbcTemplate.query(sql, locationRowMapper , *params.toTypedArray())
    }

    fun createLocation(location: Location): Int {
        val sql = """
            INSERT INTO Location (id, name, contactNumber, addressLine1, addressLine2, email, pointOfContact, pincode, state, district, taluka, city) 
            VALUES (?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?)
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
            location.district,
            location.taluka,
            location.city
        )
    }

    fun updateLocation(location: Location): Int {
        val sql = """
            UPDATE Location 
            SET name = ?, contactNumber = ?, addressLine1 = ?, addressLine2 = ?, email = ?, pointOfContact = ?, pincode = ?, state = ?, taluka = ?, district= ?, city = ?
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
            location.district,
            location.city,
            location.id
        )
    }

    fun deactivateLocation(locationId: String): Int {
        val sql = "UPDATE location SET status = 'inactive' WHERE id = ?"
        return jdbcTemplate.update(sql, locationId)
    }

    fun activateLocation(locationId: String): Int {
        val sql = "UPDATE location SET status = 'active' WHERE id = ?"
        return jdbcTemplate.update(sql, locationId)
    }

    fun deleteLocationById(id: String): Int {
        val sql = "DELETE FROM Location WHERE id = ?"
        return jdbcTemplate.update(sql, id)
    }
}
