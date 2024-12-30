package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.Employee
import com.liquifysolutions.tms.tmsbackend.repository.EmployeeRepository
import org.springframework.stereotype.Service

@Service
class EmployeeService(private val employeeRepository: EmployeeRepository) {

    // Create a new employee
    fun createEmployee(employee: Employee): Employee {
        try {
            employeeRepository.create(employee)
            return employee
        } catch (e: Exception) {
            throw e
        }

    }

    // Get an employee by ID
    fun getEmployeeById(id: String): Employee? {
        return employeeRepository.findById(id)
    }

    // Get all employees
    fun getAllEmployees(): List<Employee> {
        return employeeRepository.findAll()
    }

    // Update an existing employee by ID
    fun updateEmployee(employee: Employee): Int {
        return employeeRepository.update(employee)
    }

    // Delete an employee by ID
    fun deleteEmployeeById(id: String): Boolean {
        val rowsAffected = employeeRepository.deleteById(id)
        return rowsAffected > 0
    }
}