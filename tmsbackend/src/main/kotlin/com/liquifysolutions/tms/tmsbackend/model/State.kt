package com.liquifysolutions.tms.tmsbackend.model

data class State(
    val s_id: String,
    val name: String
)

data class StateFilterRequest(
    val state: String?
)