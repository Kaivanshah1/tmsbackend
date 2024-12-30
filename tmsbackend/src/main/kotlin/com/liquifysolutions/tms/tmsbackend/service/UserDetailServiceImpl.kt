package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException.NotFound

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {         //this function is of spring security and called during authentication to load user details by their username
        val user = userRepository.findByUsername(username) ?: throw UsernameNotFoundException("user not found")

        return User(
            user.username,
            user.passwordHash,
            listOf(SimpleGrantedAuthority("ROLE_${user.role}"))
        )
    }
}