package com.liquifysolutions.tms.tmsbackend.model

import java.util.UUID

data class Location(
    val id: String? = UUID.randomUUID().toString(),
    val name: String,
    val contactNumber: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val email: String?,
    val pointOfContact: String?,
    val pincode: String?,
    val state: String,
    val district: String,
    val taluka: String,
    val city: String,
    val status: String="active"
)

data class ListLocationsInput(
    val search: String = "",
    val page: Int = 1,
    val size: Int = 10,
    val states: List<String> = emptyList(),
    val districts: List<String> = emptyList(),
    val talukas: List<String> = emptyList(),
    val cities: List<String> = emptyList(),
    val statuses: List<String> = emptyList(),
    val getAll: Boolean = false,
)