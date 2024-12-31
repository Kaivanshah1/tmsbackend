package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.DeliveryOrder
import com.liquifysolutions.tms.tmsbackend.model.DeliveryOrderSection
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class DeliveryOrderRepository(private val jdbcTemplate: JdbcTemplate) {

    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        DeliveryOrder(
            id = rs.getString("id"),
            contractId = rs.getString("contractId"),
            partyId = rs.getString("partyId"),
            dateOfContract = rs.getLong("dateOfContract").takeIf { !rs.wasNull() },
            status = rs.getString("status"),
            deliveryOrderSections = getDeliveryOrderSections(rs.getString("id")),
            grandTotalQuantity = rs.getInt("grandTotalQuantity"),
            grandTotalPendingQuantity = rs.getInt("grandTotalPendingQuantity"),
            grandTotalInProgressQuantity = rs.getInt("grandTotalInProgressQuantity"),
            grandTotalDeliveredQuantity = rs.getInt("grandTotalDeliveredQuantity"),
            createdAt = rs.getLong("createdAt").takeIf { !rs.wasNull() },
            updatedAt = rs.getLong("updatedAt").takeIf { !rs.wasNull() }
        )
    }

    fun getDeliveryOrderSections(deliveryOrderId: String): List<DeliveryOrderSection> {
        return emptyList()
    }

    fun create(deliveryOrder: DeliveryOrder): Int {

        try {
            val sql = """
            INSERT INTO DeliveryOrder (
                id, contractId, partyId, dateOfContract, status,
                grandTotalQuantity, grandTotalPendingQuantity,
                grandTotalInProgressQuantity, grandTotalDeliveredQuantity, createdAt, updatedAt
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """
            return jdbcTemplate.update(
                sql,
                deliveryOrder.id,
                deliveryOrder.contractId,
                deliveryOrder.partyId,
                deliveryOrder.dateOfContract,
                deliveryOrder.status,
                deliveryOrder.grandTotalQuantity,
                deliveryOrder.grandTotalPendingQuantity,
                deliveryOrder.grandTotalInProgressQuantity,
                deliveryOrder.grandTotalDeliveredQuantity,
                deliveryOrder.createdAt,
                deliveryOrder.updatedAt
            )
        } catch (e: Exception) {
            throw e
        }

    }

    fun findById(id: String): DeliveryOrder? {
        val sql = "SELECT * FROM DeliveryOrder WHERE id = ?"
        return jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
    }

    fun findAll(): List<DeliveryOrder> {
        val sql = "SELECT * FROM DeliveryOrder"
        return jdbcTemplate.query(sql, rowMapper)
    }

    fun update(deliveryOrder: DeliveryOrder): Int {
        val sql = """
            UPDATE DeliveryOrder SET
                contractId = ?, partyId = ?, dateOfContract = ?, status = ?,
                grandTotalQuantity = ?, grandTotalPendingQuantity = ?,
                grandTotalInProgressQuantity = ?, grandTotalDeliveredQuantity = ?,
                createdAt = ?, updatedAt = ?
            WHERE id = ?
        """
        return jdbcTemplate.update(
            sql,
            deliveryOrder.contractId,
            deliveryOrder.partyId,
            deliveryOrder.dateOfContract,
            deliveryOrder.status,
            deliveryOrder.grandTotalQuantity,
            deliveryOrder.grandTotalPendingQuantity,
            deliveryOrder.grandTotalInProgressQuantity,
            deliveryOrder.grandTotalDeliveredQuantity,
            deliveryOrder.createdAt,
            deliveryOrder.updatedAt,
            deliveryOrder.id
        )
    }

    fun deleteById(id: String): Int {
        val sql = "DELETE FROM DeliveryOrder WHERE id = ?"
        return jdbcTemplate.update(sql, id)
    }

}
