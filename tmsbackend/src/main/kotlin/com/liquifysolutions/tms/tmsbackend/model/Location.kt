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
    val taluka: String,
    val city: String
)

