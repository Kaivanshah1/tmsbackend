package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.DeliveryOrder
import com.liquifysolutions.tms.tmsbackend.model.DeliveryOrderItem
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

    private val deliveryOrderItemRowMapper = RowMapper { rs: ResultSet, _: Int ->
        DeliveryOrderItem(
            id = rs.getString("id"),
            deliveryOrderId = rs.getString("deliveryOrderId"),
            district = rs.getString("district"),
            taluka = rs.getString("taluka"),
            locationId = rs.getString("locationId"),
            materialId = rs.getString("materialId"),
            quantity = rs.getInt("quantity"),
            rate = rs.getDouble("rate"),
            unit = rs.getString("unit"),
            dueDate = rs.getLong("dueDate"),
            status = rs.getString("status")
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
        // 1. Fetch the DeliveryOrder
        val deliveryOrderSql = "SELECT * FROM DeliveryOrder WHERE id = ?"
        val deliveryOrder = jdbcTemplate.query(deliveryOrderSql, rowMapper, id).firstOrNull() ?: return null

        // 2. Fetch DeliveryOrderItems
        val deliveryOrderItemsSql = "SELECT * FROM DeliveryOrderItem WHERE deliveryOrderId = ?"
        val deliveryOrderItems = jdbcTemplate.query(deliveryOrderItemsSql, deliveryOrderItemRowMapper, id)

        // 3. Group into DeliveryOrderSections
        val deliveryOrderSections = deliveryOrderItems.groupBy { it.district ?: "null_district" }.map { (district, items) ->
            val actualDistrict = if (district == "null_district") null else district
            DeliveryOrderSection(
                district = actualDistrict,
                totalQuantity = items.sumOf { it.quantity },
                totalPendingQuantity = items.sumOf { it.pendingQuantity ?: 0 },
                totalInProgressQuantity = items.sumOf { it.inProgressQuantity ?: 0 },
                totalDeliveredQuantity = items.sumOf { it.deliveredQuantity ?: 0 },
                status = items.firstOrNull()?.status ?: "",
                deliveryOrderItems = items
            )
        }
        // Aggregate totals for grand totals
        val grandTotalQuantity = deliveryOrderItems.sumOf { it.quantity }
        val grandTotalPendingQuantity = deliveryOrderItems.sumOf { it.pendingQuantity ?: 0 }
        val grandTotalInProgressQuantity = deliveryOrderItems.sumOf { it.inProgressQuantity ?: 0 }
        val grandTotalDeliveredQuantity = deliveryOrderItems.sumOf { it.deliveredQuantity ?: 0 }

        // 4. Return the DeliveryOrder with sections
        return deliveryOrder.copy(deliveryOrderSections = deliveryOrderSections,
            grandTotalQuantity = grandTotalQuantity,
            grandTotalPendingQuantity = grandTotalPendingQuantity,
            grandTotalInProgressQuantity = grandTotalInProgressQuantity,
            grandTotalDeliveredQuantity = grandTotalDeliveredQuantity
        )
    }

//    fun findById(id: String): DeliveryOrder? {
//        val sql = ""
//        return jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
//    }

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
