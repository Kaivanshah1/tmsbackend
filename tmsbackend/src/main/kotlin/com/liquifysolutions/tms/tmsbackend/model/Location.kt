package com.liquifysolutions.tms.tmsbackend.model

data class Location(
    val l_id: String,
    val name: String,
    val point_of_contact: String,
    val district: String,
    val taluka: String,
    val city: String,
    val email: String,
    val phone: String,
    val pincode: String,
    val address_1: String,
    val address_2: String
)