package com.liquifysolutions.tms.tmsbackend.model

import java.util.*

data class Material (
    val id: String? = UUID.randomUUID().toString(),
    val name: String
)