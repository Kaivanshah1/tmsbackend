package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.Party
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class PartyRepository(private val jdbcTemplate: JdbcTemplate) {

    private val rowMapper = RowMapper { rs, _ ->
        Party(
            id = rs.getString("id"),
            name = rs.getString("name"),
            contactNumber = rs.getString("contactNumber"),
            addressLine1 = rs.getString("addressLine1"),
            addressLine2 = rs.getString("addressLine2"),
            email = rs.getString("email"),
            pointOfContact = rs.getString("pointOfContact"),
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
        val sql = "SELECT * FROM party WHERE id = ?"
        return jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
    }

    fun createParty(party: Party): Party {
        jdbcTemplate.update(
            "INSERT INTO party (id, name, contactNumber, addressLine1, addressLine2, email, pointOfContact, pincode, state, taluka, city) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            party.id, party.name, party.contactNumber, party.addressLine1, party.addressLine2,
            party.email, party.pointOfContact, party.pincode, party.state, party.taluka, party.city
        )
        return party
    }

    fun updateParty(party: Party): Int {
        val sql = """
        UPDATE party
        SET name = ?, contactNumber = ?, addressLine1 = ?, addressLine2 = ?, email = ?, 
            pointOfContact = ?, pincode = ?, state = ?, taluka = ?, city = ?
        WHERE id = ?
    """
        return jdbcTemplate.update(
            sql,
            party.name, party.contactNumber, party.addressLine1, party.addressLine2,
            party.email, party.pointOfContact, party.pincode, party.state, party.taluka, party.city, party.id
        )
    }


    fun deletePartyById(id: String): Int {
        val sql = "DELETE FROM party WHERE id = ?"
        return jdbcTemplate.update(sql, id)
    }
}