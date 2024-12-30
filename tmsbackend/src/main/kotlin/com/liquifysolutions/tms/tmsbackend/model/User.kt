package com.liquifysolutions.tms.tmsbackend.model

import org.springframework.data.annotation.Id

data class User(        //this is used to stored in db
    var id: String? = null,
    var username: String,
    var passwordHash: String,
    var role: String = "USER",
    var refreshToken : String? = null
)

data class UserRegistrationDto( //this is used every time the registration request is send
    val username: String,
    val password: String,
    val role: String = "USER"
)

data class LoginRequest(  //this is used when login request is send
    val username: String,
    val password: String
)

data class AuthResponse(val accessToken: String, val refreshToken: String)