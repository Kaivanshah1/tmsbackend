package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.Party
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class PartyRepository(private val jdbcTemplate: JdbcTemplate) {

    private val rowMapper = RowMapper { rs, _ ->
        Party(
            p_id = rs.getString("p_id"),
            name = rs.getString("name"),
            phone = rs.getString("phone"),
            address_1 = rs.getString("address_1"),
            address_2 = rs.getString("address_2"),
            email = rs.getString("email"),
            pincode = rs.getString("pincode"),
            state = rs.getString("state"),
            taluka = rs.getString("taluka"),
            city = rs.getString("city")
        )
    }

    fun getAllParties(): List<Party> {
        val sql = "SELECT * FROM party"
        return jdbcTemplate.query(sql, rowMapper)
    }

    fun getPartyById(id: String): Party? {
        val sql = "SELECT * FROM party WHERE p_id = ?"
        return jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
    }

    fun createParty(party: Party): Party {
         jdbcTemplate.update(
            "INSERT INTO party (p_id, name, phone, address_1, address_2, email, pincode, state, taluka, city) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            party.p_id, party.name, party.phone, party.address_1, party.address_2,
            party.email, party.pincode, party.state, party.taluka, party.city
        )
        return party;
    }

    fun updateParty(party: Party): Int {
        val sql = """
            UPDATE party
            SET name = ?, phone = ?, address_1 = ?, address_2 = ?, email = ?, 
                pincode = ?, state = ?, taluka = ?, city = ?
            WHERE p_id = ?
        """
        return jdbcTemplate.update(
            sql,
            party.name, party.phone, party.address_1, party.address_2,
            party.email, party.pincode, party.state, party.taluka, party.city, party.p_id
        )
    }

    fun deletePartyById(id: String): Int {
        val sql = "DELETE FROM party WHERE p_id = ?"
        return jdbcTemplate.update(sql, id)
    }
}