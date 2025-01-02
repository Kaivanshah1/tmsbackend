package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.DeliveryChallan
import com.liquifysolutions.tms.tmsbackend.model.DeliveryChallanItem
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
    fun update(deliveryChallan: DeliveryChallan): Int {
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

            return jdbcTemplate.update(
                sql,
                deliveryChallan.dateOfChallan,
                deliveryChallan.status,
                deliveryChallan.totalDeliveringQuantity,
                currentTime,
                deliveryChallan.id
            )
        }catch (e : Exception){
            throw e;
        }
    }

    fun updateWithItems(deliveryChallan: DeliveryChallan): Int {
        val challanUpdateCount = update(deliveryChallan)

        if(challanUpdateCount > 0){

            val currentItems =   getItemsByChallanId(deliveryChallan.id!!);
            val updatedItems = deliveryChallan.deliveryChallanItems ?: emptyList()

            val itemsToDelete = currentItems.filterNot { currentItem ->  updatedItems.any { it.id == currentItem.id }}

            itemsToDelete.forEach{
                deleteItem(it)
            }

            updatedItems.forEach{ item ->
                if (currentItems.any { currentItem -> currentItem.id == item.id }) {
                    updateItem(item)
                } else {
                    createItem(item.copy(deliveryChallanId = deliveryChallan.id))
                }
            }
            return challanUpdateCount;
        }else{
            return challanUpdateCount
        }

    }

    private fun insertItem(item:DeliveryChallanItem) : Int{

        val sql = """
            INSERT INTO DeliveryChallanItems 
            (id, deliveryChallanId, deliveryOrderItemId, district, taluka, locationName, materialName, quantity, rate, dueDate, deliveringQuantity) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """

        return jdbcTemplate.update(
            sql,
            item.id,
            item.deliveryChallanId,
            item.deliveryOrderItemId,
            item.district,
            item.taluka,
            item.locationName,
            item.materialName,
            item.quantity,
            item.rate,
            item.dueDate,
            item.deliveringQuantity
        )

    }


    private fun updateItem(item:DeliveryChallanItem) : Int{
        val sql = """
            UPDATE DeliveryChallanItems
            SET
                deliveryOrderItemId = ?,
                district = ?,
                taluka = ?,
                locationName = ?,
                materialName = ?,
                quantity = ?,
                rate = ?,
                dueDate = ?,
                deliveringQuantity = ?
            WHERE id = ?
        """
        return jdbcTemplate.update(
            sql,
            item.deliveryOrderItemId,
            item.district,
            item.taluka,
            item.locationName,
            item.materialName,
            item.quantity,
            item.rate,
            item.dueDate,
            item.deliveringQuantity,
            item.id
        )

    }

    private fun deleteItem(item:DeliveryChallanItem):Int {
        val sql = "DELETE FROM DeliveryChallanItems WHERE id = ?"
        return jdbcTemplate.update(sql,item.id)

    }
    fun getById(id: String): DeliveryChallan? {
        val sql = """ 
                SELECT 
                dc.*, 
                p.name AS partyname 
            FROM 
                deliverychallan AS dc
            JOIN 
                deliveryorder AS d_orders ON dc.deliveryorderid = d_orders.id
            JOIN 
                party AS p ON d_orders.partyid = p.id
			WHERE dc.id = ?
        """.trimIndent()
        return jdbcTemplate.query(sql, deliveryChallanRowMapper, id).firstOrNull()
    }


    fun listAll(): List<DeliveryChallan> {
        val sql = "SELECT * FROM DeliveryChallan"
        return jdbcTemplate.query(sql, deliveryChallanRowMapper)
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

    fun createItems(deliveryChallanItems: List<DeliveryChallanItem>): Int {
        if (deliveryChallanItems.isEmpty()) return 0

        val sql = """
        INSERT INTO DeliveryChallanItems 
        (id, deliveryChallanId, deliveryOrderItemId, district, taluka, locationName, materialName, quantity, rate, dueDate, deliveringQuantity) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """

        return try {
            deliveryChallanItems.forEach { item ->
                jdbcTemplate.update(
                    sql,
                    item.id,
                    item.deliveryChallanId,
                    item.deliveryOrderItemId,
                    item.district,
                    item.taluka,
                    item.locationName,
                    item.materialName,
                    item.quantity,
                    item.rate,
                    item.dueDate,
                    item.deliveringQuantity
                )
            }
            deliveryChallanItems.size
        } catch (e: Exception) {
            // Handle the exception, e.g., log it
            throw e
        }
    }

    private fun getItemsByChallanId(challanId: String): List<DeliveryChallanItem> {
        val sql = "SELECT * FROM DeliveryChallanItems WHERE deliveryChallanId = ?"
        return jdbcTemplate.query(sql, deliveryChallanItemRowMapper, challanId)
    }
}