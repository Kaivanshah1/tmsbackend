package com.liquifysolutions.tms.tmsbackend.controller

import com.liquifysolutions.tms.tmsbackend.model.Employee
import com.liquifysolutions.tms.tmsbackend.service.EmployeeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/api/v1/employees")
class EmployeeController(private val employeeService: EmployeeService) {
    // Create a new employee
    @PostMapping("/create")
    fun createEmployee(@RequestBody employee: Employee): ResponseEntity<Employee> {
        val createdEmployee = employeeService.createEmployee(employee)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee)
    }

    // Get an employee by ID
    @GetMapping("/get/{id}")
    fun getEmployeeById(@PathVariable id: String): ResponseEntity<Employee?> {
        val employee = employeeService.getEmployeeById(id)
        return if (employee != null) ResponseEntity.ok(employee) else ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }

    // Get all employees
    @PostMapping("/list")
    fun getAllEmployees(): ResponseEntity<List<Employee>> {
        val employees = employeeService.getAllEmployees()
        return ResponseEntity.ok(employees)
    }

    // Update an employee by ID
    @PostMapping("/update")
    fun updateEmployee(@RequestBody employee: Employee): ResponseEntity<String> {
        val updatedEmployee = employeeService.updateEmployee(employee)
        return  ResponseEntity.ok("Employee updated successfully")
    }

    // Delete an employee by ID
    @DeleteMapping("/{id}")
    fun deleteEmployeeById(@PathVariable id: String): ResponseEntity<Void> {
        val isDeleted = employeeService.deleteEmployeeById(id)
        return if (isDeleted) ResponseEntity.noContent().build() else ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }
}
