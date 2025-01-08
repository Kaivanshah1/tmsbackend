package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.Employee
import com.liquifysolutions.tms.tmsbackend.model.ListEmployeesInput
import com.liquifysolutions.tms.tmsbackend.model.UserRegistrationDto
import com.liquifysolutions.tms.tmsbackend.repository.EmployeeRepository
import com.liquifysolutions.tms.tmsbackend.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.UUID

@Service
class EmployeeService(private val employeeRepository: EmployeeRepository, private val authService: AuthService, private val userRepository: UserRepository) {

    // Create a new employee
    fun createEmployee(employee: Employee): Employee {
        val employeeToCreate: Employee = employee.copy(
            id = UUID.randomUUID().toString(),
            createdAt = Instant.now().epochSecond
        )

        employeeRepository.create(employeeToCreate)
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
    fun getAllEmployees(request: ListEmployeesInput): List<Employee> {
        return employeeRepository.findAll(
            search = request.search,
            roles = request.roles,
            statuses = request.statuses,
            page = request.page,
            size = request.size
        )
    }
    // Update an existing employee by ID
    fun updateEmployee(employee: Employee): Int {
        return employeeRepository.update(employee)
    }

    @Transactional
    fun deactivateEmployee(employeeId: String) {
        val employee = employeeRepository.findById(employeeId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found with id $employeeId")
        val rowsUpdated = employeeRepository.deactivateEmployee(employeeId)
        if(rowsUpdated == 0){
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to deactivate the employee")
        }
        val email = employee.email;
        val user = userRepository.findByUsername(email);
        if(user != null){
            val rowsDeleted = userRepository.deleteUserByEmail(email)
            if(rowsDeleted == 0){
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete the user")
            }
        }
    }

    // Delete an employee by ID
    fun deleteEmployeeById(id: String): Boolean {
        val rowsAffected = employeeRepository.deleteById(id)
        return rowsAffected > 0
    }
}