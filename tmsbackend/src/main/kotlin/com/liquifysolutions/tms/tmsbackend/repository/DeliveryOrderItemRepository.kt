package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.DeliveryOrderItem
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class DeliveryOrderItemRepository(private val jdbcTemplate: JdbcTemplate) {

    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        DeliveryOrderItem(
            id = rs.getString("id"),
            deliveryOrderId = rs.getString("deliveryOrderId"),
            district = rs.getString("district"),
            taluka = rs.getString("taluka"),
            locationId = rs.getString("locationId"),
            materialId = rs.getString("materialId"),
            quantity = rs.getInt("quantity"),
            rate = rs.getDouble("rate").takeIf { !rs.wasNull() },
            unit = rs.getString("unit"),
            dueDate = rs.getLong("dueDate").takeIf { !rs.wasNull() },
            status = rs.getString("status")
        )
    }

    fun create(deliveryOrderItem: DeliveryOrderItem): Int {
        val sql = """
            INSERT INTO DeliveryOrderItem (
                id, deliveryOrderId, district, taluka, locationId, materialId, quantity,
               rate, unit, dueDate, status
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """
        return jdbcTemplate.update(
            sql,
            deliveryOrderItem.id,
            deliveryOrderItem.deliveryOrderId,
            deliveryOrderItem.district,
            deliveryOrderItem.taluka,
            deliveryOrderItem.locationId,
            deliveryOrderItem.materialId,
            deliveryOrderItem.quantity,
            deliveryOrderItem.rate,
            deliveryOrderItem.unit,
            deliveryOrderItem.dueDate,
            deliveryOrderItem.status
        )
    }

    fun insertItems(items: List<DeliveryOrderItem>) {
        if (items.isEmpty()) return
        val sql = """
            INSERT INTO DeliveryOrderItem (
                id, deliveryOrderId, district, taluka, locationId, materialId, quantity,
               rate, unit, dueDate, status
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """
        try {
            items.forEach { item ->
                jdbcTemplate.update(
                    sql,
                    item.id,
                    item.deliveryOrderId,
                    item.district,
                    item.taluka,
                    item.locationId,
                    item.materialId,
                    item.quantity,
                    item.rate ?: 0.0,
                    item.unit,
                    item.dueDate ?: 0,
                    item.status
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun getExistingItems(deliveryOrderId: String): List<DeliveryOrderItem> {
        val sql = "SELECT * FROM DeliveryOrderItem WHERE deliveryOrderId = ?"
        return jdbcTemplate.query(sql, rowMapper, deliveryOrderId)
    }

    fun saveAll(items: List<DeliveryOrderItem>, deliveryOrderId: String) {
        val sql = """
            INSERT INTO DeliveryOrderItem (
                id, deliveryOrderId, district, taluka, locationId, materialId, quantity,
               rate, unit, dueDate, status
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """

        try {
            items.forEach { item ->
                jdbcTemplate.update(
                    sql,
                    item.id,
                    item.deliveryOrderId,
                    item.district,
                    item.taluka,
                    item.locationId,
                    item.materialId,
                    item.quantity,
                    item.rate ?: 0.0,
                    item.unit,
                    item.dueDate ?: 0,
                    item.status
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun updateItems(items: List<DeliveryOrderItem>) {
        if (items.isEmpty()) return
        val sql = """
        UPDATE DeliveryOrderItem 
        SET 
            district = ?, 
            taluka = ?, 
            locationId = ?, 
            materialId = ?, 
            quantity = ?, 
            rate = ?, 
            unit = ?, 
            dueDate = ?, 
            status = ?
        WHERE id = ? AND deliveryOrderId = ?
    """

        try {
            items.forEach { item ->
                jdbcTemplate.update(
                    sql,
                    item.district,
                    item.taluka,
                    item.locationId,
                    item.materialId,
                    item.quantity,
                    item.rate ?: 0,
                    item.unit,
                    item.dueDate ?: 0,
                    item.status,
                    item.id,
                    item.deliveryOrderId
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun syncItems(items: List<DeliveryOrderItem>, deliveryOrderId: String) {
        val existingItems = getExistingItems(deliveryOrderId)

        val itemsToUpdate = mutableListOf<DeliveryOrderItem>()
        val itemsToDelete = mutableListOf<DeliveryOrderItem>()
        val itemsToInsert = mutableListOf<DeliveryOrderItem>()

        items.forEach { item ->
            val existingItem = existingItems.find { it.id == item.id }

            if (existingItem != null) {
                val mergedItem = item.copy(
                    district =  item.district ?: existingItem.district,
                    taluka =  item.taluka ?: existingItem.taluka,
                    locationId = item.locationId ?: existingItem.locationId,
                    materialId = item.materialId ?: existingItem.materialId,
                    unit = item.unit ?: existingItem.unit,
                    dueDate = item.dueDate ?: existingItem.dueDate,
                    deliveryOrderId = existingItem.deliveryOrderId
                )
                itemsToUpdate.add(mergedItem)
            } else {
                itemsToInsert.add(item.copy(deliveryOrderId = deliveryOrderId))
            }
        }

        existingItems.forEach { existingItem ->
            if (!items.any { it.id == existingItem.id }) {
                itemsToDelete.add(existingItem)
            }
        }
        updateItems(itemsToUpdate)
        insertItems(itemsToInsert)
        deleteItems(itemsToDelete)
    }

    fun deleteItems(items: List<DeliveryOrderItem>) {
        if (items.isEmpty()) return
        val sql = "DELETE FROM DeliveryOrderItem WHERE id = ? AND deliveryOrderId = ?"
        try {
            items.forEach { item ->
                jdbcTemplate.update(sql, item.id, item.deliveryOrderId)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun findByDeliveryOrderId(deliveryOrderId: String): List<DeliveryOrderItem> {
        val sql = "SELECT * FROM DeliveryOrderItem WHERE deliveryOrderId = ?"
        return jdbcTemplate.query(sql, rowMapper, deliveryOrderId)
    }
}