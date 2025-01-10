package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.Date
import java.sql.ResultSet
import java.time.Instant

@Repository
class DeliveryChallanRepository(private val jdbcTemplate: JdbcTemplate) {

    private val deliveryChallanRowMapper = RowMapper { rs: ResultSet, _: Int ->
        DeliveryChallan(
            id = rs.getString("dc_number"),
            deliveryOrderId = rs.getString("do_number"),
            dateOfChallan = rs.getLong("dateofchallan"),
            status = rs.getString("status"),
            totalDeliveringQuantity = rs.getDouble("totaldeliveringquantity"),
            createdAt = rs.getLong("createdat"),
            updatedAt = rs.getLong("updatedat"),
            deliveryChallanItems = emptyList(),
            transportationCompanyId = rs.getString("transportationcompanyid"),
            vehicleId = rs.getString("vehicleid"),
            driverId = rs.getString("driverid"),
            partyName = rs.getString("name")
        )
    }

    private val deliveryChallanItemRowMapper = RowMapper { rs: ResultSet, _: Int ->
        DeliveryChallanItem(
            id = rs.getString("id"),
            deliveryChallanId = rs.getString("dc_number"),
            deliveryOrderItemId = rs.getString("deliveryorderitemid"),
            district = rs.getString("district"),
            taluka = rs.getString("taluka"),
            locationId = rs.getString("locationid"),
            materialId = rs.getString("materialid"),
            quantity = rs.getDouble("quantity"),
            rate = rs.getDouble("rate"),
            dueDate = rs.getLong("duedate"),
            deliveringQuantity = rs.getDouble("deliveringquantity"),
            deliveredQuantity = rs.getDouble("deliveredquantity"),
        )
    }

    private fun mapRowToDeliveryChallanOutputRecord(rs: ResultSet): DeliveryChallanOutputRecord =
        DeliveryChallanOutputRecord(
            id = rs.getString("dc_number"),
            deliveryOrderId = rs.getString("do_number"),
            dateOfChallan = rs.getLong("dateofchallan"),
            status = rs.getString("status"),
            partyName = rs.getString("partyname"),
            totalDeliveringQuantity = rs.getDouble("totaldeliveringquantity"),
            transportationCompanyName = rs.getString("transportationcompanyname"),
            driverName = rs.getString("drivername")
        )

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
                updatedat = ?,
                transportationcompanyid = ?,
                driverid = ?,
                vehicleid = ?
            WHERE dc_number = ?
        """

            val currentTime = Instant.now().toEpochMilli()

            jdbcTemplate.update(
                sql,
                deliveryChallan.dateOfChallan,
                deliveryChallan.status,
                deliveryChallan.totalDeliveringQuantity,
                currentTime,
                deliveryChallan.id,
                deliveryChallan.transportationCompanyId,
                deliveryChallan.driverId,
                deliveryChallan.vehicleId
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
            p.name,
            dc.totaldeliveringquantity,
            dc.createdat,
            dc.updatedat,
            dc.transportationcompanyid,
            dc.driverid,
            dc.vehicleid
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
            dci.locationid,
            dci.materialid,
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
                    deliveryOrderId = rs.getString("do_number"),
                    dateOfChallan = rs.getLong("dateofchallan"),
                    status = rs.getString("status"),
                    totalDeliveringQuantity = rs.getDouble("totaldeliveringquantity"),
                    createdAt = rs.getLong("createdat"),
                    updatedAt = rs.getLong("updatedat"),
                    transportationCompanyId = rs.getString("transportationcompanyid"),
                    driverId = rs.getString("driverid"),
                    vehicleId = rs.getString("vehicleid"),
                    partyName = rs.getString("name")
                    )
            }, id)
                .firstOrNull()
                ?.let { deliveryChallan ->
                    val deliveryChallanItems = jdbcTemplate.query(itemsSql,{rs, _ ->
                        DeliveryChallanItem(
                            id = rs.getString("id"),
                            deliveryChallanId = rs.getString("dc_number"),
                            deliveryOrderItemId = rs.getString("deliveryorderitemid"),
                            district = rs.getString("district"),
                            taluka = rs.getString("taluka"),
                            locationId = rs.getString("locationid"),
                            materialId = rs.getString("materialid"),
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

    fun listAll(request: ListDeliveryChallansInput): List<DeliveryChallanOutputRecord> {
        try {
            val page = (request.page ?: 1).coerceAtLeast(1)
            val size = (request.size ?: 10).coerceIn(1, 100)
            val offset = ((page - 1) * size).coerceAtLeast(0)

            val params = mutableListOf<Any>()
            val whereClauses = mutableListOf<String>()

            // Search by DC Number
            if (!request.search.isNullOrBlank()) {
                whereClauses.add("dc.dc_number ILIKE ?")
                params.add("%${request.search}%")
            }

            // Filter by Delivery Order IDs
            if (!request.deliveryOrderIds.isNullOrEmpty()) {
                val placeholders = request.deliveryOrderIds.joinToString(", ") { "?" }
                whereClauses.add("dc.do_number IN ($placeholders)")
                params.addAll(request.deliveryOrderIds)
            }

            // Filter by Statuses
            if (!request.statuses.isNullOrEmpty()) {
                val placeholders = request.statuses.joinToString(", ") { "?" }
                whereClauses.add("dc.status IN ($placeholders)")
                params.addAll(request.statuses)
            }

            // Filter by Party IDs
            if (!request.partyIds.isNullOrEmpty()) {
                val placeholders = request.partyIds.joinToString(", ") { "?" }
                whereClauses.add("d.partyid IN ($placeholders)")
                params.addAll(request.partyIds)
            }

            // Filter by Transportation Company IDs
            if (!request.transportationCompanyIds.isNullOrEmpty()) {
                val placeholders = request.transportationCompanyIds.joinToString(", ") { "?" }
                whereClauses.add("dc.transportationcompanyid IN ($placeholders)")
                params.addAll(request.transportationCompanyIds)
            }

            // Filter by Date Range
            if (request.fromDate != null) {
                val date =  Date(request.fromDate)
                whereClauses.add("dc.dateofchallan >= ?")
                params.add(date)
            }

            if (request.toDate != null) {
                val date =  Date(request.toDate)
                whereClauses.add("dc.dateofchallan <= ?")
                params.add(date)
            }

            val whereClause = if (whereClauses.isNotEmpty()) "WHERE " + whereClauses.joinToString(" AND ") else ""


            val sql = """
                SELECT 
                    dc.dc_number,
                    dc.do_number,
                    dc.dateofchallan,
                    dc.status,
                    p.name as partyname,
                    COALESCE(
                    (
                        SELECT SUM(dci.deliveringquantity)
                        FROM deliverychallanitems dci
                        WHERE dci.dc_number = dc.dc_number
                    ),
                    0.0
                ) as totaldeliveringquantity,
                tc.companyname as transportationcompanyname,
                dr.name AS drivername
                FROM deliverychallan as dc
                JOIN deliveryorder as d ON dc.do_number = d.do_number
                JOIN party as p ON d.partyid = p.id
                 LEFT JOIN 
                    transportationcompany tc ON dc.transportationcompanyid = tc.id
                 LEFT JOIN 
                    driver dr ON dc.driverid = dr.id
                 $whereClause
                ORDER BY dc.createdat DESC
                ${if (!request.getAll) "LIMIT ? OFFSET ?" else ""}
            """.trimIndent()

            if (!request.getAll) {
                params.add(size)
                params.add(offset)
            }

            return jdbcTemplate.query(sql, { rs, _ ->
                mapRowToDeliveryChallanOutputRecord(rs)
            }, *params.toTypedArray())

        } catch (e: Exception) {
            throw e
        }
    }


    fun create(deliveryChallan: DeliveryChallan): Int {
        try {
            val sql = """
            INSERT INTO DeliveryChallan
            (dc_number, do_number, dateofchallan, status, totaldeliveringquantity, createdat, updatedat, transportationcompanyid, vehicleid, driverid)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """
            return jdbcTemplate.update(
                sql,
                deliveryChallan.id,
                deliveryChallan.deliveryOrderId,
                deliveryChallan.dateOfChallan,
                deliveryChallan.status,
                deliveryChallan.totalDeliveringQuantity,
                deliveryChallan.createdAt,
                deliveryChallan.updatedAt,
                deliveryChallan.vehicleId,
                deliveryChallan.driverId,
                deliveryChallan.transportationCompanyId
                )
        }
        catch (e: Exception){
            throw e;
        }
    }

    fun createItem(deliveryChallanItem: DeliveryChallanItem): Int {
        val sql = """
        INSERT INTO DeliveryChallanItems 
        (id, dc_number, deliveryorderitemid, district, taluka, locationid, materialid, quantity, rate, duedate, deliveringquantity) 
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
                deliveryChallanItem.locationId,
                deliveryChallanItem.materialId,
                deliveryChallanItem.quantity,
                deliveryChallanItem.rate,
                deliveryChallanItem.dueDate,
                deliveryChallanItem.deliveringQuantity
            )
        } catch (e: Exception) {
            throw e
        }
    }

    fun getItemsByChallanId(challanId: String): List<DeliveryChallanItem> {
        try {
            val sql = """
            SELECT 
                dci.*,
                doi.district,
                doi.taluka,
                loc.name AS locationname,
                mat.name AS materialname,
                doi.quantity,
                doi.rate,
                doi.duedate,
                COALESCE(SUM(CASE 
                    WHEN dc.status = 'delivered' THEN dci.deliveringquantity 
                    ELSE 0 
                END), 0) AS deliveredquantity
                
                FROM 
                    deliverychallanitems dci
                JOIN 
                    location loc ON dci.locationid = loc.id
                JOIN 
                    material mat ON dci.materialid = mat.id
                LEFT JOIN 
                    deliveryorderitem doi ON dci.deliveryorderitemid = doi.id
                LEFT JOIN
                    deliverychallan dc ON dci.dc_number = dc.dc_number
                WHERE 
                    dci.dc_number = ?
                GROUP BY 
                    dci.id, doi.id, doi.district, doi.taluka, loc.name, mat.name, 
                    doi.quantity, doi.rate, doi.duedate
        """.trimIndent()
            return jdbcTemplate.query(sql, deliveryChallanItemRowMapper, challanId)
        }catch (e: Exception){
            throw e;
        }
    }
}