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
            createdAt = rs.getLong("createdAt"),
            status = rs.getString("status"),
        )
    }

    // Create
    fun create(employee: Employee): Int {
        val sql = """
            INSERT INTO employees (id, name, email, contactNumber, role, createdAt, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
    """
        return jdbcTemplate.update(
            sql,
            employee.id,
            employee.name,
            employee.email,
            employee.contactNumber,
            employee.role,
            employee.createdAt,
            employee.status
        )
    }

    // Read
    fun findById(id: String): Employee? {
        val sql = "SELECT * FROM employees WHERE id = ?"
        return jdbcTemplate.queryForObject(sql, rowMapper, id)
    }

    fun findAll(
        search: String?,
        roles: List<String>?,
        statuses: List<String>?,
        page: Int,
        size: Int
    ): List<Employee> {
        val baseSql = StringBuilder("SELECT id, name, email, contactNumber, role, createdat, status FROM employees WHERE 1=1")
        val params = mutableListOf<Any>()

        // Add search condition
        if (!search.isNullOrBlank()) {
            baseSql.append(" AND name ILIKE ?")
            params.add("%$search%")
        }

        // Add roles filter
        if (!roles.isNullOrEmpty()) {
            baseSql.append(" AND role IN (${roles.joinToString { "?" }})")
            params.addAll(roles)
        }

        // Add statuses filter
        if (!statuses.isNullOrEmpty()) {
            baseSql.append(" AND status IN (${statuses.joinToString { "?" }})")
            params.addAll(statuses)
        }

        // Add pagination
        baseSql.append(" LIMIT ? OFFSET ?")
        params.add(size)
        params.add((page - 1) * size)

        // Execute query
        return jdbcTemplate.query(baseSql.toString(), params.toTypedArray(), rowMapper)
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

    fun deactivateEmployee(employeeId: String): Int {
        try {
            val sql = "UPDATE employees SET status = 'inactive' WHERE id = ?"
            return jdbcTemplate.update(sql, employeeId)
        }catch (e: Exception){
            throw e;
        }
    }

    // Delete
    fun deleteById(id: String): Int {
        val sql = "DELETE FROM employees WHERE id = ?"
        return jdbcTemplate.update(sql, id)
    }
}