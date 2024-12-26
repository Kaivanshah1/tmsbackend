package com.liquifysolutions.tms.tmsbackend.model

data class City(
    val c_id: String,
    val name: String,
    val taluka_id: String
)

data class TalukaFilterRequest(
    val state: String?,
    val district: String?
)

data class CityFilterRequest(
    val state: String?,
    val district: String?,
    val taluka: String?
)