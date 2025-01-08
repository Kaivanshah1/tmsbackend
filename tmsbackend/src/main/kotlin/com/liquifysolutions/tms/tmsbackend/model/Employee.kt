package com.liquifysolutions.tms.tmsbackend.model

import java.util.*

data class Employee (
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val contactNumber: String?,
    val role: String,
    val createdAt: Long?,
    val status: String = "active",
)

data class ListEmployeesInput(
    val search: String = "",
    val roles: List<String> = emptyList(),
    val statuses: List<String> = emptyList(),
    val page: Int = 1,
    val size: Int = 10
)