package com.liquifysolutions.tms.tmsbackend.model

import java.util.UUID

data class User(        //this is used to stored in db
    var id: String? = UUID.randomUUID().toString(),
    var username: String,
    var email: String?,
    var passwordHash: String,
    var role: String = "USER",
    var refreshToken : String? = null
)

data class UserRegistrationDto( //this is used every time the registration request is send
    val username: String,
    val password: String,
    val email: String,
    val role: String = "USER"
)

data class LoginRequest(  //this is used when login request is send
    val email: String,
    val password: String
)

data class AuthResponse(val accessToken: String, val refreshToken: String)