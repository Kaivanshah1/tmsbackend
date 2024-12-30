package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.User
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class UserRepository(private val jdbcTemplate: JdbcTemplate) {

    private val rowMapper = RowMapper<User> { rs, _ ->
        User(
            id = rs.getString("id"),
            username = rs.getString("username"),
            passwordHash = rs.getString("passwordhash"),
            role = rs.getString("role"),
            refreshToken = rs.getString("refreshtoken")
        )
    }

    fun findByUsername(username: String): User? {
        val sql = "SELECT * FROM users WHERE username = ?"
        return jdbcTemplate.queryForObject(sql, rowMapper, username)
    }

    fun save(user: User): User {
        try {
            val sql = """
            INSERT INTO users (id, username, passwordhash, role, refreshtoken)
            VALUES (?, ?, ?, ?, ?)
        """
            jdbcTemplate.update(
                sql,
                user.id ?: java.util.UUID.randomUUID().toString(), // Generate UUID if id is null
                user.username,
                user.passwordHash,
                user.role,
                user.refreshToken
            )
            return user
        }catch (e: Exception){
            println(e);
            return user;
        }
    }
}
