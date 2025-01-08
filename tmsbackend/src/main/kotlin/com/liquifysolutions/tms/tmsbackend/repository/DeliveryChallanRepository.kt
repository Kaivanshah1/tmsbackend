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
            id = rs.getString("dc_number"),
            do_number = rs.getString("do_number"),
            dateOfChallan = rs.getLong("dateofchallan"),
            status = rs.getString("status"),
            totalDeliveringQuantity = rs.getDouble("totaldeliveringquantity"),
            createdAt = rs.getLong("createdat"),
            updatedAt = rs.getLong("updatedat"),
            deliveryChallanItems = emptyList()
        )
    }

    private val deliveryChallanItemRowMapper = RowMapper { rs: ResultSet, _: Int ->
        DeliveryChallanItem(
            id = rs.getString("id"),
            dc_number = rs.getString("dc_number"),
            deliveryOrderItemId = rs.getString("deliveryorderitemid"),
            district = rs.getString("district"),
            taluka = rs.getString("taluka"),
            locationName = rs.getString("locationname"),
            materialName = rs.getString("materialname"),
            quantity = rs.getDouble("quantity"),
            rate = rs.getDouble("rate"),
            dueDate = rs.getLong("duedate"),
            deliveringQuantity = rs.getDouble("deliveringquantity")
        )
    }

    fun getDeliveryChallanCount(): Int? {
        val sql = "SELECT COUNT(*) FROM deliverychallan"
        return jdbcTemplate.queryForObject(sql, Int::class.java)
    }

    fun update(deliveryChallan: DeliveryChallan): DeliveryChallan {
        try {
            val sql = """
            UPDATE deliverychallan
            SET
                dateofchallan = ?,
                status = ?,
                totaldeliveringquantity = ?,
                updatedat = ?
            WHERE dc_number = ?
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
            dc.dc_number,
            dc.do_number,
            dc.dateofchallan,
            dc.status,
            p.name AS partyname,
            dc.totaldeliveringquantity,
            dc.createdat,
            dc.updatedat
        FROM deliverychallan dc
        JOIN deliveryorder d_orders ON dc.do_number = d_orders.do_number
        JOIN party p ON d_orders.partyid = p.id
        WHERE dc.dc_number = ?
    """.trimIndent()

    val itemsSql = """
       SELECT
            dci.id,
            dci.dc_number,
            dci.deliveryorderitemid,
            dci.district,
            dci.taluka,
            dci.locationname,
            dci.materialname,
            dci.quantity,
            dci.rate,
            dci.duedate,
            dci.deliveringquantity
            FROM deliverychallanitems dci
        WHERE dci.dc_number = ?
    """.trimIndent()

    fun getById(id: String): DeliveryChallan? {
        return try {
            jdbcTemplate.query(sql, { rs, _ ->
                DeliveryChallan(
                    id = rs.getString("dc_number"),
                    do_number = rs.getString("do_number"),
                    dateOfChallan = rs.getLong("dateofchallan"),
                    status = rs.getString("status"),
                    totalDeliveringQuantity = rs.getDouble("totaldeliveringquantity"),
                    createdAt = rs.getLong("createdat"),
                    updatedAt = rs.getLong("updatedat")
                )
            }, id)
                .firstOrNull()
                ?.let { deliveryChallan ->
                    val deliveryChallanItems = jdbcTemplate.query(itemsSql,{rs, _ ->
                        DeliveryChallanItem(
                            id = rs.getString("id"),
                            dc_number = rs.getString("dc_number"),
                            deliveryOrderItemId = rs.getString("deliveryorderitemid"),
                            district = rs.getString("district"),
                            taluka = rs.getString("taluka"),
                            locationName = rs.getString("locationname"),
                            materialName = rs.getString("materialname"),
                            quantity = rs.getDouble("quantity"),
                            rate = rs.getDouble("rate"),
                            dueDate = rs.getLong("duedate"),
                            deliveringQuantity = rs.getDouble("deliveringquantity")
                        )
                    },deliveryChallan.id)
                    deliveryChallan.copy(deliveryChallanItems = deliveryChallanItems)
                }
        } catch (e: Exception) {
            println("Error fetching DeliveryChallan: ${e.message}")
            throw e
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
            val page = (request.page ?: 1).coerceAtLeast(1)
            val size = (request.size ?: 10).coerceIn(1, 100)
            val offset = ((page - 1) * size).coerceAtLeast(0)

            val sql = """
            SELECT 
                dc.*
            FROM deliverychallan as dc
            JOIN deliveryorder as d ON dc.do_number = d.do_number
            JOIN party as p ON d.partyid = p.id
            ORDER BY dc.createdat DESC
            LIMIT ? OFFSET ?
        """.trimIndent()

            return jdbcTemplate.query(sql, { rs, _ ->
                DeliveryChallan(
                    id = rs.getString("dc_number"),
                    do_number = rs.getString("do_number"),
                    dateOfChallan = rs.getLong("dateofchallan"),
                    status = rs.getString("status"),
                    totalDeliveringQuantity = rs.getDouble("totaldeliveringquantity"),
                    createdAt = rs.getLong("createdat"),
                    updatedAt = rs.getLong("updatedat")
                )
            }, size, offset)
        } catch (e: Exception) {
            throw e;
        }
    }

    fun create(deliveryChallan: DeliveryChallan): Int {
        try {
            val sql = """
            INSERT INTO DeliveryChallan
            (dc_number, do_number, dateofchallan, status, totaldeliveringquantity, createdat, updatedat)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """
            return jdbcTemplate.update(
                sql,
                deliveryChallan.id,
                deliveryChallan.do_number,
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
        (id, dc_number, deliveryorderitemid, district, taluka, locationname, materialname, quantity, rate, duedate, deliveringquantity) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """

        return try {
            jdbcTemplate.update(
                sql,
                deliveryChallanItem.id,
                deliveryChallanItem.dc_number,
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
        val sql = "SELECT * FROM DeliveryChallanItems WHERE dc_number = ?"
        return jdbcTemplate.query(sql, deliveryChallanItemRowMapper, challanId)
    }
}