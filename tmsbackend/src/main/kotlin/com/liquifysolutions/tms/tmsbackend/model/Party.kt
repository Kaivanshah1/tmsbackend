package com.liquifysolutions.tms.tmsbackend.model

data class Party(
    val p_id: String,
    val name: String,
    val phone: String,
    val address_1: String,
    val address_2: String,
    val email: String,
    val pincode: String,
    val state: String,
    val taluka: String,
    val city: String
)
