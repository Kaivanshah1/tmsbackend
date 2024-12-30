package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.Employee
import com.liquifysolutions.tms.tmsbackend.model.UserRegistrationDto
import com.liquifysolutions.tms.tmsbackend.repository.EmployeeRepository
import org.springframework.stereotype.Service

@Service
class EmployeeService(private val employeeRepository: EmployeeRepository, private val authService: AuthService) {

    // Create a new employee
    fun createEmployee(employee: Employee): Employee {
        employeeRepository.create(employee)
//        val password = authService.generateRandomPassword(6)
        val password = "123456"
        val createdUser = UserRegistrationDto(username = employee.name, email = employee.email, password = password)
        authService.registerUser(createdUser)
        return employee
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