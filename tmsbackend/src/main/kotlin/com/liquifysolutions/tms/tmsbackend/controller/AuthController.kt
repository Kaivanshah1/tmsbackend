package com.liquifysolutions.tms.tmsbackend.controller

import com.liquifysolutions.tms.tmsbackend.model.AuthResponse
import com.liquifysolutions.tms.tmsbackend.model.LoginRequest
import com.liquifysolutions.tms.tmsbackend.model.User
import com.liquifysolutions.tms.tmsbackend.model.UserRegistrationDto
import com.liquifysolutions.tms.tmsbackend.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(val authService: AuthService) {
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<AuthResponse>{
        return ResponseEntity.ok(authService.loginUser(loginRequest.email, loginRequest.password))
    }

    @PostMapping("/refresh")
    fun refreshToken( @RequestParam token: String): ResponseEntity<Any?>{
        return ResponseEntity.ok(authService.refreshToken(token))
    }
}