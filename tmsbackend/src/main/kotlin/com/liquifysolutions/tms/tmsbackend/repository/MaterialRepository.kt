package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.Material
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.UUID

@Repository
class MaterialRepository(private val jdbcTemplate: JdbcTemplate) {

    private val logger = LoggerFactory.getLogger(MaterialRepository::class.java)

    private val rowMapper = RowMapper<Material> { rs: ResultSet, _: Int ->
        Material(
            id = rs.getString("id"),
            name = rs.getString("name")
        )
    }

    // Create
    fun create(material: Material): Int {
        val sql = "INSERT INTO material (id, name) VALUES (?, ?)"
        val id = material.id ?: UUID.randomUUID().toString()
        return jdbcTemplate.update(sql, id, material.name)
    }

    // Read by ID
    fun findById(id: String): Material? {
        val sql = "SELECT * FROM material WHERE id = ?"
        return try {
            jdbcTemplate.queryForObject(sql, rowMapper, id).also {
            }
        } catch (e: Exception) {
            null
        }
    }

    // Read all
    fun findAll(): List<Material> {
        val sql = "SELECT * FROM material"
        logger.info("Fetching all materials")
        return try {
            jdbcTemplate.query(sql, rowMapper).also {
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Update
    fun update(material: Material): Int {
        val sql = "UPDATE material SET name = ? WHERE id = ?"
        return try {
            jdbcTemplate.update(sql, material.name, material.id).also {
            }
        } catch (e: Exception) {
            throw e;
        }
    }

    // Delete
    fun deleteById(id: String): Int {
        val sql = "DELETE FROM material WHERE id = ?"
        logger.info("Deleting material with ID: {}", id)
        return try {
            jdbcTemplate.update(sql, id).also {
            }
        } catch (e: Exception) {
            throw e;
        }
    }
}
