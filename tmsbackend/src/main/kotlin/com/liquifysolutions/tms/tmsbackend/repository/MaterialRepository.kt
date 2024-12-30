package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.Material
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.UUID

@Repository
class MaterialRepository(private val jdbcTemplate: JdbcTemplate) {

    private val rowMapper = RowMapper<Material> { rs: ResultSet, _: Int ->
        Material(
            id = rs.getString("id"),
            name = rs.getString("name")
        )
    }

    // Create
    fun create(material: Material): Int {
        val sql = "INSERT INTO materials (id, name) VALUES (?, ?)"
        return jdbcTemplate.update(
            sql,
            material.id ?: UUID.randomUUID().toString(),
            material.name
        )
    }

    // Read by ID
    fun findById(id: String): Material? {
        val sql = "SELECT * FROM materials WHERE id = ?"
        return jdbcTemplate.queryForObject(sql, rowMapper, id)
    }

    // Read all
    fun findAll(): List<Material> {
        val sql = "SELECT * FROM materials"
        return jdbcTemplate.query(sql, rowMapper)
    }

    // Update
    fun update(material: Material): Int {
        val sql = "UPDATE materials SET name = ? WHERE id = ?"
        return jdbcTemplate.update(sql, material.name, material.id)
    }

    // Delete
    fun deleteById(id: String): Int {
        val sql = "DELETE FROM materials WHERE id = ?"
        return jdbcTemplate.update(sql, id)
    }
}