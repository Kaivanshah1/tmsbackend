package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.DeliveryChallan
import com.liquifysolutions.tms.tmsbackend.model.DeliveryChallanItem
import com.liquifysolutions.tms.tmsbackend.model.ListDeliveryChallansInput
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.Instant

@Repository
class DeliveryChallanRepository(private val jdbcTemplate: JdbcTemplate) {

    private val deliveryChallanRowMapper = RowMapper { rs: ResultSet, _: Int ->
        DeliveryChallan(
            id = rs.getString("id"),
            deliveryOrderId = rs.getString("deliveryOrderId"),
            dateOfChallan = rs.getLong("dateOfChallan"),
            status = rs.getString("status"),
            partyName = rs.getString("partyName"),
            totalDeliveringQuantity = rs.getDouble("totalDeliveringQuantity"),
            createdAt = rs.getLong("createdAt"),
            updatedAt = rs.getLong("updatedAt"),
            deliveryChallanItems = emptyList()
        )
    }

    private val deliveryChallanItemRowMapper = RowMapper { rs: ResultSet, _: Int ->
        DeliveryChallanItem(
            id = rs.getString("id"),
            deliveryChallanId = rs.getString("deliveryChallanId"),
            deliveryOrderItemId = rs.getString("deliveryOrderItemId"),
            district = rs.getString("district"),
            taluka = rs.getString("taluka"),
            locationName = rs.getString("locationName"),
            materialName = rs.getString("materialName"),
            quantity = rs.getDouble("quantity"),
            rate = rs.getDouble("rate"),
            dueDate = rs.getLong("dueDate"),
            deliveringQuantity = rs.getDouble("deliveringQuantity")
        )
    }

    fun update(deliveryChallan: DeliveryChallan): DeliveryChallan {
        try {
            val sql = """
            UPDATE deliverychallan
            SET
                dateofchallan = ?,
                status = ?,
                totaldeliveringquantity = ?,
                updatedAt = ?
            WHERE id = ?
        """

            val currentTime = Instant.now().toEpochMilli()

            jdbcTemplate.update(
                sql,
                deliveryChallan.dateOfChallan,
                deliveryChallan.status,
                deliveryChallan.totalDeliveringQuantity,
                currentTime,
                deliveryChallan.id
            )

            val existingItems = getItemsByChallanId(deliveryChallan.id!!)
            val newItems = deliveryChallan.deliveryChallanItems

            val existingItemIds = existingItems.mapNotNull { it.id }.toSet()
            val newItemIds = newItems.mapNotNull { it.id }.toSet()

            val itemsToCreate = newItems.filter { it.id == null || !existingItemIds.contains(it.id) }
            val itemsToUpdate = newItems.filter { it.id != null && existingItemIds.contains(it.id) }
            val itemsToDelete = existingItems.filter { it.id != null && !newItemIds.contains(it.id) }

            // Delete removed items
            if (itemsToDelete.isNotEmpty()) {
                val deleteSql = """
                    DELETE FROM deliverychallanitems
                    WHERE id = ?
                """.trimIndent()
                itemsToDelete.forEach { item ->
                    jdbcTemplate.update(deleteSql, item.id)
                }
            }

            // Update existing items
            val updateItemSql = """
                UPDATE deliverychallanitems
                SET
                    deliveringquantity = ?
                WHERE id = ?
            """.trimIndent()

            itemsToUpdate.forEach { item ->
                jdbcTemplate.update(
                    updateItemSql,
                    item.deliveringQuantity,
                    item.id
                )
            }

            // Create new items
            itemsToCreate.forEach { item ->
                createItem(item)
            }

            return deliveryChallan
        }catch (e: Exception){
            throw e;
        }
    }

    val sql = """
        SELECT
            dc.id,
            dc.deliveryorderid,
            dc.dateofchallan,
            dc.status,
            p.name AS partyname,
            dc.totaldeliveringquantity,
            dc.createdat,
            dc.updatedat,
            dci.id as item_id,
            dci.deliverychallanid,
            dci.deliveryorderitemid,
            dci.district,
            dci.taluka,
            dci.locationname,
            dci.materialname,
            dci.quantity,
            dci.rate,
            dci.duedate,
            dci.deliveringquantity
        FROM deliverychallan dc
        JOIN deliveryorder d_orders ON dc.deliveryorderid = d_orders.id
        JOIN party p ON d_orders.partyid = p.id
        LEFT JOIN deliverychallanitems dci ON dc.id = dci.deliverychallanid
        WHERE dc.id = ?
    """.trimIndent()

    fun getById(id: String): DeliveryChallan? {
        return try {
            jdbcTemplate.query(sql, { rs, _ ->
                ChallanRow(
                    challanId = rs.getString("id"),
                    deliveryOrderId = rs.getString("deliveryorderid"),
                    dateOfChallan = rs.getLong("dateofchallan"),
                    status = rs.getString("status"),
                    partyName = rs.getString("partyname"),
                    totalDeliveringQuantity = rs.getDouble("totaldeliveringquantity"),
                    createdAt = rs.getLong("createdat"),
                    updatedAt = rs.getLong("updatedat"),
                    itemId = rs.getString("item_id"), // Handle potential NULL
                    district = rs.getString("district"), // Handle potential NULL
                    taluka = rs.getString("taluka"), // Handle potential NULL
                    locationName = rs.getString("locationname"), // Handle potential NULL
                    materialName = rs.getString("materialname"), // Handle potential NULL
                    quantity = rs.getDouble("quantity"),
                    rate = rs.getDouble("rate"),
                    dueDate = rs.getLong("duedate"),
                    deliveringQuantity = rs.getDouble("deliveringquantity"),
                    deliveryOrderItemId = rs.getString("deliveryorderitemid")
                )
            }, id)
                .takeIf { it.isNotEmpty() }
                ?.let { rows ->
                    val firstRow = rows.first()
                    DeliveryChallan(
                        id = firstRow.challanId,
                        deliveryOrderId = firstRow.deliveryOrderId,
                        dateOfChallan = firstRow.dateOfChallan,
                        status = firstRow.status,
                        partyName = firstRow.partyName,
                        totalDeliveringQuantity = firstRow.totalDeliveringQuantity,
                        createdAt = firstRow.createdAt,
                        updatedAt = firstRow.updatedAt,
                        deliveryChallanItems = rows.map { row ->
                            DeliveryChallanItem(
                                id = row.itemId,
                                deliveryChallanId = row.challanId,
                                deliveryOrderItemId = row.deliveryOrderItemId,
                                district = row.district,
                                taluka = row.taluka,
                                locationName = row.locationName,
                                materialName = row.materialName,
                                quantity = row.quantity,
                                rate = row.rate,
                                dueDate = row.dueDate,
                                deliveringQuantity = row.deliveringQuantity
                            )
                        }
                    )
                }
        } catch (e: Exception) {
            // Log the error for debugging (optional)
            println("Error fetching DeliveryChallan: ${e.message}")
            throw e // Re-throw the exception for higher-level handling
        }
    }


    private data class ChallanRow(
        val challanId: String,
        val deliveryOrderId: String,
        val dateOfChallan: Long,
        val status: String,
        val partyName: String,
        val totalDeliveringQuantity: Double,
        val createdAt: Long,
        val updatedAt: Long,
        val itemId: String,
        val district: String?,
        val taluka: String?,
        val locationName: String?,
        val materialName: String?,
        val quantity: Double,
        val rate: Double,
        val dueDate: Long?,
        val deliveringQuantity: Double,
        val deliveryOrderItemId: String?
    )


    fun listAll(request: ListDeliveryChallansInput): List<DeliveryChallan> {
        try {
            val page = request.page ?: 1
            val size = request.size ?: 10
            val offset = (page - 1) * size

            val sql = """
            SELECT 
                dc.*,
                p.name as partyName
            FROM deliverychallan as dc
            JOIN deliveryorder as d ON dc.deliveryorderid = d.id
            JOIN party as p ON d.partyid = p.id
            ORDER BY dc.createdat DESC
            LIMIT ? OFFSET ?
        """.trimIndent()

            return jdbcTemplate.query(sql, { rs, _ ->
                DeliveryChallan(
                    id = rs.getString("id"),
                    deliveryOrderId = rs.getString("deliveryorderid"),
                    dateOfChallan = rs.getLong("dateofchallan"),
                    status = rs.getString("status"),
                    totalDeliveringQuantity = rs.getDouble("totaldeliveringquantity"),
                    createdAt = rs.getLong("createdAt"),
                    updatedAt = rs.getLong("updatedAt"),
                    partyName = rs.getString("partyName")
                )
            }, size, offset)
        }catch (e: Exception){
            println("Error while fetching delivery challan $e")
            return emptyList()
        }
    }

    fun create(deliveryChallan: DeliveryChallan): Int {
        try {
            val sql = """
            INSERT INTO DeliveryChallan
            (id, deliveryOrderId, dateOfChallan, status, totalDeliveringQuantity, createdAt, updatedAt)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """
            return jdbcTemplate.update(
                sql,
                deliveryChallan.id,
                deliveryChallan.deliveryOrderId,
                deliveryChallan.dateOfChallan,
                deliveryChallan.status,
                deliveryChallan.totalDeliveringQuantity,
                deliveryChallan.createdAt,
                deliveryChallan.updatedAt)
        }
        catch (e: Exception){
            throw e;
        }
    }

    fun createItem(deliveryChallanItem: DeliveryChallanItem): Int {
        val sql = """
        INSERT INTO DeliveryChallanItems 
        (id, deliveryChallanId, deliveryOrderItemId, district, taluka, locationName, materialName, quantity, rate, dueDate, deliveringQuantity) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """

        return try {
            jdbcTemplate.update(
                sql,
                deliveryChallanItem.id,
                deliveryChallanItem.deliveryChallanId,
                deliveryChallanItem.deliveryOrderItemId,
                deliveryChallanItem.district,
                deliveryChallanItem.taluka,
                deliveryChallanItem.locationName,
                deliveryChallanItem.materialName,
                deliveryChallanItem.quantity,
                deliveryChallanItem.rate,
                deliveryChallanItem.dueDate,
                deliveryChallanItem.deliveringQuantity
            )
        } catch (e: Exception) {
            // Handle the exception, e.g., log it
            throw e
        }
    }

    fun getItemsByChallanId(challanId: String): List<DeliveryChallanItem> {
        val sql = "SELECT * FROM DeliveryChallanItems WHERE deliveryChallanId = ?"
        return jdbcTemplate.query(sql, deliveryChallanItemRowMapper, challanId)
    }
}