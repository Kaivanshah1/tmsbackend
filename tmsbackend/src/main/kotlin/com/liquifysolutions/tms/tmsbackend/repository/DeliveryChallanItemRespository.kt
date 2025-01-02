package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.DeliveryChallanItems
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class DeliveryChallanItemRespository(private val jdbcTemplate: JdbcTemplate) {
    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        DeliveryChallanItems(
            id = rs.getString("id"),
            district = rs.getString("district"),
            taluka = rs.getString("taluka"),
            location = rs.getString("location"),
            weight = rs.getDouble("weight").takeIf { !rs.wasNull() },
            do_id = rs.getString("do_id"),
            expectedQuantity = rs.getDouble("expectedQuantity").takeIf { !rs.wasNull() }
        )
    }

    fun create(deliveryChallanItem: DeliveryChallanItems): Int {
        val sql = "INSERT INTO DeliveryChallanItems (id, weight, do_id, expectedQuantity) VALUES (?, ?, ?, ?)"
        return jdbcTemplate.update(
            sql,
            deliveryChallanItem.id,
            deliveryChallanItem.weight,
            deliveryChallanItem.do_id,
            deliveryChallanItem.expectedQuantity
        )
    }
}