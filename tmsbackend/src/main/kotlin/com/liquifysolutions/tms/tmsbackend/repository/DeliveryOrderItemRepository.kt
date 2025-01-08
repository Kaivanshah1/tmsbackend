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
            do_number = rs.getString("do_number"),
            district = rs.getString("district"),
            taluka = rs.getString("taluka"),
            locationId = rs.getString("locationid"),
            materialId = rs.getString("materialid"),
            quantity = rs.getDouble("quantity"),
            rate = rs.getDouble("rate").takeIf { !rs.wasNull() },
            unit = rs.getString("unit"),
            dueDate = rs.getLong("duedate").takeIf { !rs.wasNull() }
        )
    }

    fun create(deliveryOrderItem: DeliveryOrderItem): Int {
        val sql = """
            INSERT INTO DeliveryOrderItem (
                id, do_number, district, taluka, locationid, materialid, quantity,
               rate, unit, duedate
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """
        return jdbcTemplate.update(
            sql,
            deliveryOrderItem.id,
            deliveryOrderItem.do_number,
            deliveryOrderItem.district,
            deliveryOrderItem.taluka,
            deliveryOrderItem.locationId,
            deliveryOrderItem.materialId,
            deliveryOrderItem.quantity,
            deliveryOrderItem.rate,
            deliveryOrderItem.unit,
            deliveryOrderItem.dueDate,
        )
    }

    fun insertItems(items: List<DeliveryOrderItem>) {
        if (items.isEmpty()) return
        val sql = """
            INSERT INTO DeliveryOrderItem (
                id, do_number, district, taluka, locationid, materialid, quantity,
               rate, unit, duedate
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """
        try {
            items.forEach { item ->
                jdbcTemplate.update(
                    sql,
                    item.id,
                    item.do_number,
                    item.district,
                    item.taluka,
                    item.locationId,
                    item.materialId,
                    item.quantity,
                    item.rate ?: 0.0,
                    item.unit,
                    item.dueDate ?: 0,
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun getExistingItems(deliveryOrderId: String): List<DeliveryOrderItem> {
        val sql = "SELECT * FROM DeliveryOrderItem WHERE do_number = ?"
        return jdbcTemplate.query(sql, rowMapper, deliveryOrderId)
    }

    fun saveAll(items: List<DeliveryOrderItem>, deliveryOrderId: String) {
        val sql = """
            INSERT INTO DeliveryOrderItem (
                id, do_number, district, taluka, locationid, materialid, quantity,
               rate, unit, duedate
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """

        try {
            items.forEach { item ->
                jdbcTemplate.update(
                    sql,
                    item.id,
                    item.do_number,
                    item.district,
                    item.taluka,
                    item.locationId,
                    item.materialId,
                    item.quantity,
                    item.rate ?: 0.0,
                    item.unit,
                    item.dueDate ?: 0,
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

     fun updateItems(items: List<DeliveryOrderItem>) {
        try {
        if (items.isEmpty()) return
        val sql = """
        UPDATE DeliveryOrderItem 
        SET 
            district = ?, 
            taluka = ?, 
            locationid = ?, 
            materialid = ?, 
            quantity = ?, 
            rate = ?, 
            unit = ?, 
            duedate = ?
        WHERE id = ? AND do_number = ?
    """

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
                    item.id,
                    item.do_number
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
                    do_number = existingItem.do_number
                )
                itemsToUpdate.add(mergedItem)
            } else {
                itemsToInsert.add(item.copy(do_number = deliveryOrderId))
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
        val sql = "DELETE FROM DeliveryOrderItem WHERE id = ? AND do_number = ?"
        try {
            items.forEach { item ->
                jdbcTemplate.update(sql, item.id, item.do_number)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun findByDeliveryOrderId(deliveryOrderId: String): List<DeliveryOrderItem> {
        val sql = "SELECT * FROM DeliveryOrderItem WHERE do_number = ?"
        return jdbcTemplate.query(sql, rowMapper, deliveryOrderId)
    }
}