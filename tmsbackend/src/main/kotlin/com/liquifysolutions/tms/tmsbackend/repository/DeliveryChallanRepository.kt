package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.DeliveryChallan
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class DeliveryChallanRepository(private val jdbcTemplate: JdbcTemplate)  {

    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        DeliveryChallan(
            id = rs.getString("id"),
            status = rs.getString("status"),
            do_id = rs.getString("do_id"),
            date = rs.getLong("date"),
            dc_items = emptyList(),
        )
    }

    fun getById(id: String): DeliveryChallan? {
        val sql = "SELECT * FROM DeliveryChallan WHERE id = ?"
        return jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
    }

    fun listAll(): List<DeliveryChallan> {
        val sql = "SELECT * FROM DeliveryChallan"
        return jdbcTemplate.query(sql, rowMapper)
    }

    fun create(deliveryChallan: DeliveryChallan): Int {
        val sql = "INSERT INTO DeliveryChallan (id, status, do_id, date) VALUES (?, ?, ?, ?)"
        return jdbcTemplate.update(sql, deliveryChallan.id, deliveryChallan.status, deliveryChallan.do_id)
    }
}