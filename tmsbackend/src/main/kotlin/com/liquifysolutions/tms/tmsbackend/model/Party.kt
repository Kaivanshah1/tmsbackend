package com.liquifysolutions.tms.tmsbackend.model

import java.util.UUID

data class Party(
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
    val city: String,
    val status: String = "active",
)


data class ListPartiesInput(
    val search: String? = null,
    val getAll: Boolean? = false,
    val statuses: List<String>? = null,
    val page: Int = 1,
    val size: Int = 10
)