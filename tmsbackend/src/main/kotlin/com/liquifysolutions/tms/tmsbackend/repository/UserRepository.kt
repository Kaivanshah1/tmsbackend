package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.User
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val jdbcTemplate: JdbcTemplate) {

    private val rowMapper = RowMapper<User> { rs, _ ->
        User(
            id = rs.getString("id"),
            username = rs.getString("username"),
            email = rs.getString("email"),
            passwordHash = rs.getString("passwordhash"),
            role = rs.getString("role"),
            refreshToken = rs.getString("refreshtoken")
        )
    }

    fun findByUsername(username: String): User? {
        val sql = "SELECT * FROM users WHERE email = ?"
        return jdbcTemplate.queryForObject(sql, rowMapper, username)
    }

    fun save(user: User): User {
        try {
            val sql = """
            INSERT INTO users (id, username, email, passwordhash, role, refreshtoken)
            VALUES (?, ?, ?, ?, ?, ?)
        """
            jdbcTemplate.update(
                sql,
                user.id ?: java.util.UUID.randomUUID().toString(), // Generate UUID if id is null
                user.username,
                user.email,
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

    fun update(user: User): User {
        try {


            val sql = "UPDATE users SET username = ?, email = ?, passwordhash = ?, role = ?, refreshtoken = ? WHERE id = ?"

            jdbcTemplate.update(
                sql,
                user.username,
                user.email,
                user.passwordHash,
                user.role,
                user.refreshToken,
                user.id
            )
            return user
        }catch (e: Exception){
            println(e);
            return user;
        }
    }

    fun deleteUserByEmail(email: String): Int {
        val sql = "DELETE FROM users WHERE email = ?"
        return jdbcTemplate.update(sql, email)
    }
}
