package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
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
            partyname = rs.getString("partyname"),
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

    private val deliveryOrderItemMetaDataMapper = RowMapper { rs: ResultSet, _: Int ->
        DeliveryOrderItemMetaData(
            id = rs.getString("id"),
            district = rs.getString("district"),
            taluka = rs.getString("taluka"),
            locationName = rs.getString("locationName"),
            materialName = rs.getString("materialName"),
            quantity = rs.getInt("quantity"),
            status = rs.getString("status"),
            rate = rs.getDouble("rate"),
            dueDate = rs.getLong("duedate"),
        )
    }

    fun getDeliveryOrderSections(deliveryOrderId: String): List<DeliveryOrderSection> {
        return emptyList()
    }

    fun getDeliveryOrderItemById(deliveryOrderId: String): List<DeliveryOrderItemMetaData> {
        return try {
            val sql = """
            select 
                doi.id,
                doi.district,
                doi.taluka,
                doi.quantity,
                doi.status,
                doi.rate,
                doi.duedate,
                m.name as materialName,
                lo.name as locationName
            from deliveryorderitem as doi
            join location as lo on lo.id = doi.locationid
            join material as m on m.id = doi.materialid
            where doi.deliveryorderid = ?
        """.trimIndent()

            val items = jdbcTemplate.query(sql, deliveryOrderItemMetaDataMapper, deliveryOrderId)

            // Fetch related DeliveryChallanItems and calculate delivered and inProgress quantities
            items.forEach { item ->
                calculateDeliveredAndInProgressQuantities(item)
            }

            return items
        } catch (e: Exception) {
            throw e;
        }
    }

    private fun calculateDeliveredAndInProgressQuantities(item: DeliveryOrderItemMetaData) {
        println("Calculating quantities for DeliveryOrderItem ID: ${item.id}")

        val challanItemsSql = """
            SELECT
                dc.status,
                dci.deliveringquantity
            FROM deliverychallanitems as dci
            JOIN deliverychallan as dc ON dci.deliverychallanid = dc.id
            WHERE dci.deliveryorderitemid = ?
        """.trimIndent()

        val deliveryChallanItems = jdbcTemplate.query(
            challanItemsSql,
            { rs, _ ->
                object {
                    val status = rs.getString("status")
                    val deliveringQuantity = rs.getDouble("deliveringquantity")
                }
            }, item.id
        )
        println("Number of deliveryChallanItems found: ${deliveryChallanItems.size}")


        var totalDeliveredQuantity = 0.0
        var totalInProgressQuantity = 0.0

        deliveryChallanItems.forEach {
            println("ChallanItem status: ${it.status}, deliveringQuantity: ${it.deliveringQuantity}")
            when (it.status) {
                "DELIVERED" -> totalDeliveredQuantity += it.deliveringQuantity
                "IN_PROGRESS" -> totalInProgressQuantity += it.deliveringQuantity
                "pending" -> totalInProgressQuantity += it.deliveringQuantity  // Treat "pending" as in-progress
            }
        }

        println("Total Delivered Quantity : ${totalDeliveredQuantity}")
        println("Total InProgress Quantity : ${totalInProgressQuantity}")


        item.deliveredQuantity = totalDeliveredQuantity
        item.inProgressQuantity = totalInProgressQuantity
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
        try {
            val deliveryOrderSql = "SELECT * FROM DeliveryOrder WHERE id = ?"
            val deliveryOrder = jdbcTemplate.query(deliveryOrderSql, rowMapper, id).firstOrNull() ?: return null

            val deliveryOrderItemsSql = "SELECT * FROM DeliveryOrderItem WHERE deliveryOrderId = ?"
            val deliveryOrderItems = jdbcTemplate.query(deliveryOrderItemsSql, deliveryOrderItemRowMapper, id)

            deliveryOrderItems.forEach{ item ->
                calculateDeliveredAndInProgressQuantities(item)
            }

            val deliveryOrderSections =
                deliveryOrderItems.groupBy { it.district ?: "null_district" }.map { (district, items) ->
                    val actualDistrict = if (district == "null_district") null else district
                    DeliveryOrderSection(
                        district = actualDistrict,
                        totalQuantity = items.sumOf { it.quantity },
                        totalPendingQuantity = items.sumOf { it.pendingQuantity ?: 0 },
                        totalInProgressQuantity = items.sumOf { it.inProgressQuantity ?: 0},
                        totalDeliveredQuantity = items.sumOf { it.deliveredQuantity ?: 0 },
                        status = items.firstOrNull()?.status ?: "",
                        deliveryOrderItems = items
                    )
                }

            val grandTotalQuantity = deliveryOrderItems.sumOf { it.quantity }
            val grandTotalPendingQuantity = deliveryOrderItems.sumOf { it.pendingQuantity ?: 0 }
            val grandTotalInProgressQuantity = deliveryOrderItems.sumOf { it.inProgressQuantity ?: 0 }
            val grandTotalDeliveredQuantity = deliveryOrderItems.sumOf { it.deliveredQuantity ?: 0 }

            return deliveryOrder.copy(
                deliveryOrderSections = deliveryOrderSections,
                grandTotalQuantity = grandTotalQuantity,
                grandTotalPendingQuantity = grandTotalPendingQuantity,
                grandTotalInProgressQuantity = grandTotalInProgressQuantity,
                grandTotalDeliveredQuantity = grandTotalDeliveredQuantity
            )
        }catch (e: Exception){
            throw e;
        }
    }

    fun calculateDeliveredAndInProgressQuantities(item: DeliveryOrderItem) {
        println("Calculating quantities for DeliveryOrderItem ID: ${item.id}")
        val challanItemsSql = """
            SELECT
                dc.status,
                dci.deliveringquantity
            FROM deliverychallanitems as dci
            JOIN deliverychallan as dc ON dci.deliverychallanid = dc.id
            WHERE dci.deliveryorderitemid = ?
        """.trimIndent()

        val deliveryChallanItems = jdbcTemplate.query(
            challanItemsSql,
            { rs, _ ->
                object {
                    val status = rs.getString("status")
                    val deliveringQuantity = rs.getDouble("deliveringquantity")
                }
            }, item.id
        )
        println("Number of deliveryChallanItems found: ${deliveryChallanItems.size}")


        var totalDeliveredQuantity = 0.0
        var totalInProgressQuantity = 0.0

        deliveryChallanItems.forEach {
            println("ChallanItem status: ${it.status}, deliveringQuantity: ${it.deliveringQuantity}")
            when (it.status) {
                "DELIVERED" -> totalDeliveredQuantity += it.deliveringQuantity
                "IN_PROGRESS" -> totalInProgressQuantity += it.deliveringQuantity
                "pending" -> totalInProgressQuantity += it.deliveringQuantity  // Treat "pending" as in-progress
            }
        }
        println("Total Delivered Quantity : ${totalDeliveredQuantity}")
        println("Total InProgress Quantity : ${totalInProgressQuantity}")


        item.deliveredQuantity = totalDeliveredQuantity.toInt()
        item.inProgressQuantity = totalInProgressQuantity.toInt()
        item.pendingQuantity = item.quantity - (totalDeliveredQuantity.toInt() + totalInProgressQuantity.toInt())
    }

    fun findAll(limit: Int, offset: Int): List<ListDeliveryOrderItem> {
        val sql = """
        SELECT 
            d.id AS id,
            d.contractId,
            d.partyId,
            p.name AS partyname,
            d.status,
            d.createdAt
        FROM DeliveryOrder d
        LEFT JOIN Party p ON d.partyId = p.id
        ORDER BY d.createdAt DESC
        LIMIT ? OFFSET ?
    """
        return jdbcTemplate.query(sql, { rs, _ ->
            ListDeliveryOrderItem(
                id = rs.getString("id"),
                contractId = rs.getString("contractId"),
                partyId = rs.getString("partyId"),
                partyname = rs.getString("partyname"),
                status = rs.getString("status"),
                createdAt = rs.getLong("createdAt"),
            )
        }, limit, offset)
    }

    @Transactional
    fun update(deliveryOrder: DeliveryOrder): Int? {
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
