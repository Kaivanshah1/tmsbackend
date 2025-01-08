package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.Party
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Pageable

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
            city = rs.getString("city"),
            status = rs.getString("status")
        )
    }

    fun findParties(
        search: String?,
        statuses: List<String>?,
        page: Int?,
        size: Int?,
        getAll: Boolean? = false
    ): List<Party> {
        val whereClauses = mutableListOf<String>()
        val params = mutableListOf<Any>()

        if (!search.isNullOrBlank()) {
            whereClauses.add("name ILIKE ?")
            params.add("%${search}%")
        }

        if (!statuses.isNullOrEmpty()) {
            whereClauses.add("status IN (${statuses.joinToString { "?" }})")
            params.addAll(statuses)
        }

        val whereClause = if (whereClauses.isNotEmpty()) "WHERE " + whereClauses.joinToString(" AND ") else ""
        var sql = "SELECT id, name, contactnumber, addressline1, addressline2, email, pointofcontact, pincode, state, taluka, city, status FROM party $whereClause ORDER BY createdat DESC"
        if(getAll == true){
            return jdbcTemplate.query(sql, rowMapper, *params.toTypedArray())
        }
        if(page != null && size != null){
            sql += " LIMIT ? OFFSET ?"
            params.add(size)
            params.add((page - 1) * size)
        }

        return jdbcTemplate.query(sql, rowMapper, *params.toTypedArray())
    }

    fun getPartyById(id: String): Party? {
        val sql = "SELECT * FROM party WHERE id = ?"
        return jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
    }

    fun createParty(party: Party): Party {
        jdbcTemplate.update(
            "INSERT INTO party (id, name, contactNumber, addressLine1, addressLine2, email, pointOfContact, pincode, state, taluka, city, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            party.id, party.name, party.contactNumber, party.addressLine1, party.addressLine2,
            party.email, party.pointOfContact, party.pincode, party.state, party.taluka, party.city,
            party.status
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

    fun deactivateParty(partyId: String): Int {
        val sql = "UPDATE party SET status = 'inactive' WHERE id = ?"
        return jdbcTemplate.update(sql, partyId)
    }

    fun activateParty(partyId: String): Int {
        val sql = "UPDATE party SET status = 'active' WHERE id = ?"
        return jdbcTemplate.update(sql, partyId)
    }

    fun deletePartyById(id: String): Int {
        val sql = "DELETE FROM party WHERE id = ?"
        return jdbcTemplate.update(sql, id)
    }
}