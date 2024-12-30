package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.Employee
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class EmployeeRepository(private val jdbcTemplate: JdbcTemplate) {

    private val rowMapper = RowMapper<Employee> { rs: ResultSet, _: Int ->
        Employee(
            id = rs.getString("id"),
            name = rs.getString("name"),
            email = rs.getString("email"),
            contactNumber = rs.getString("contactNumber"),
            role = rs.getString("role"),
            createdAt = rs.getLong("createdAt")
        )
    }

    // Create
    fun create(employee: Employee): Int {
        val sql = """
            INSERT INTO employees (id, name, email, contactNumber, role, createdAt)
            VALUES (?, ?, ?, ?, ?, ?)
    """
        return jdbcTemplate.update(
            sql,
            employee.id,
            employee.name,
            employee.email,
            employee.contactNumber,
            employee.role,
            employee.createdAt
        )
    }

    // Read
    fun findById(id: String): Employee? {
        val sql = "SELECT * FROM employees WHERE id = ?"
        return jdbcTemplate.queryForObject(sql, rowMapper, id)
    }

    fun findAll(): List<Employee> {
        val sql = "SELECT * FROM employees"
        return jdbcTemplate.query(sql, rowMapper)
    }

    // Update
    fun update(employee: Employee): Int {
        val sql = """
            UPDATE employees 
            SET name = ?, email = ?, contactNumber = ?, role = ?, createdAt = ?
            WHERE id = ?
        """
        return jdbcTemplate.update(
            sql,
            employee.name,
            employee.email,
            employee.contactNumber,
            employee.role,
            employee.createdAt,
            employee.id
        )
    }

    // Delete
    fun deleteById(id: String): Int {
        val sql = "DELETE FROM employees WHERE id = ?"
        return jdbcTemplate.update(sql, id)
    }
}