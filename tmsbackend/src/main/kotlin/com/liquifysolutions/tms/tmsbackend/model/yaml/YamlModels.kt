package com.liquifysolutions.tms.tmsbackend.model.yaml

data class YamlLocation(
    val name: String,
    val districts: List<YamlDistrict>
)

data class LocationsWrapper(
    val locations: List<YamlLocation>
)

data class YamlDistrict(
    val name: String,
    val talukas: List<YamlTaluka>
)

data class YamlTaluka(
    val name: String,
    val cities: List<String>
)