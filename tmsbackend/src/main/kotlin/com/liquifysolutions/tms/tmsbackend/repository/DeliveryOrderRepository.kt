package com.liquifysolutions.tms.tmsbackend.repository

import com.liquifysolutions.tms.tmsbackend.model.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet

@Repository
class DeliveryOrderRepository(private val jdbcTemplate: JdbcTemplate, private val deliveryOrderItemRepository: DeliveryOrderItemRepository) {

    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        DeliveryOrder(
            id = rs.getString("do_number"),
            contractId = rs.getString("contractid"),
            partyId = rs.getString("partyid"),
            dateOfContract = rs.getLong("dateofcontract").takeIf { !rs.wasNull() },
            status = rs.getString("status"),
            deliveryOrderSections = getDeliveryOrderSections(rs.getString("do_number")),
            createdat = rs.getLong("createdat").takeIf { !rs.wasNull() },
            updatedat = rs.getLong("updatedat").takeIf { !rs.wasNull() }
        )
    }

    private val deliveryOrderItemRowMapper = RowMapper { rs: ResultSet, _: Int ->
        DeliveryOrderItem(
            id = rs.getString("id"),
            do_number = rs.getString("do_number"),
            district = rs.getString("district"),
            taluka = rs.getString("taluka"),
            locationId = rs.getString("locationid"),
            materialId = rs.getString("materialid"),
            quantity = rs.getDouble("quantity"),
            rate = rs.getDouble("rate"),
            unit = rs.getString("unit"),
            dueDate = rs.getLong("duedate"),
        )
    }

    private val deliveryOrderItemMetaDataMapper = RowMapper { rs: ResultSet, _: Int ->
        DeliveryOrderItemMetaData(
            id = rs.getString("id"),
            district = rs.getString("district"),
            taluka = rs.getString("taluka"),
            locationName = rs.getString("locationname"),
            materialName = rs.getString("materialname"),
            quantity = rs.getInt("quantity"),
//            status = rs.getString("status"),
            rate = rs.getDouble("rate"),
            dueDate = rs.getLong("duedate"),
        )
    }

    fun getDeliveryOrderSections(deliveryOrderId: String): List<DeliveryOrderSection> {
        return emptyList()
    }

    fun getLastDoNumber(): String? {
        val sql = "SELECT MAX(do_number) FROM deliveryorder"
        return jdbcTemplate.queryForObject(sql, String::class.java)
    }

    fun getDeliveryOrderItemById(deliveryOrderId: String): List<DeliveryOrderItemMetaData> {
        return try {
            val sql = """
            select 
                doi.id,
                doi.district,
                doi.taluka,
                doi.quantity,
                doi.rate,
                doi.duedate,
                m.name as materialname,
                lo.name as locationname
            from deliveryorderitem as doi
            join location as lo on lo.id = doi.locationid
            join material as m on m.id = doi.materialid
            where doi.do_number = ?
        """.trimIndent()

            val items = jdbcTemplate.query(sql, deliveryOrderItemMetaDataMapper, deliveryOrderId)

            items.forEach { item ->
                calculateDeliveredAndInProgressQuantities(item)
            }

            return items
        } catch (e: Exception) {
            throw e;
        }
    }

    fun calculateDeliveredAndInProgressQuantities(item: DeliveryOrderItemMetaData) {
        val challanItemsSql = """
            SELECT
                dc.status,
                dci.deliveringquantity
            FROM deliverychallanitems as dci
            JOIN deliverychallan as dc ON dci.dc_number = dc.dc_number
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

        var totalDeliveredQuantity = 0.0

        deliveryChallanItems.forEach {
           when (it.status) {
                "DELIVERED" -> totalDeliveredQuantity += it.deliveringQuantity // Treat "pending" as in-progress
            }
        }
        item.deliveredQuantity = totalDeliveredQuantity
    }

    fun create(deliveryOrder: DeliveryOrder): Int {
        try {
            val sql = """
            INSERT INTO DeliveryOrder (
                 do_number, contractId, partyId, dateOfContract, status,
                 createdAt, updatedAt
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

            return jdbcTemplate.update(
                sql,
                deliveryOrder.id,
                deliveryOrder.contractId,
                deliveryOrder.partyId,
                deliveryOrder.dateOfContract,
                deliveryOrder.status,
                deliveryOrder.createdat,
                deliveryOrder.updatedat
            )
        } catch (e: Exception) {
            throw e
        }
    }

    fun findById(id: String): DeliveryOrder? {
        try {
            val deliveryOrderSql = "SELECT * FROM DeliveryOrder WHERE do_number = ?"
            val deliveryOrder = jdbcTemplate.query(deliveryOrderSql, rowMapper, id).firstOrNull() ?: return null

            val deliveryOrderItemsSql = "SELECT * FROM DeliveryOrderItem WHERE do_number = ?"
            val deliveryOrderItems = jdbcTemplate.query(deliveryOrderItemsSql, deliveryOrderItemRowMapper, id)

            deliveryOrderItems.forEach{ item ->
                calculateDeliveredQuantities(item)
            }

            val deliveryOrderSections =
                deliveryOrderItems.groupBy { it.district ?: "null_district" }.map { (district, items) ->
                    val actualDistrict = if (district == "null_district") null else district
                    DeliveryOrderSection(
                        district = actualDistrict,
                        totalQuantity = items.sumOf { it.quantity ?: 0.0 },
                        totalDeliveredQuantity = items.sumOf { it.deliveredQuantity ?: 0.0 },
                        deliveryOrderItems = items
                    )
                }

            val grandTotalQuantity = deliveryOrderItems.sumOf { it.quantity ?: 0.0 }
            val grandTotalDeliveredQuantity = deliveryOrderItems.sumOf { it.deliveredQuantity ?: 0.0 }

            val deliveryChallanItemsSql = """
               SELECT dci.deliveryorderitemid, dc.dc_number AS deliveryChallanId, dci.deliveringquantity
                FROM deliveryChallanItem dci
                JOIN deliverychallan dc ON dci.dc_number = dc.dc_number
                WHERE dc.do_number = ?
            """
            val deliveryChallanItems = jdbcTemplate.query(deliveryChallanItemsSql,
                RowMapper{rs, _ ->
                    AssociatedDeliverChallanItemMetadata(
                        id = rs.getString("deliveryOrderItemId"),
                        deliveringQuantity = rs.getDouble("deliveringQuantity"),
                        deliveryChallanId = rs.getString("deliveryChallanId")
                    )
                }, id
            )

            val deliveryChallanItemsGroupedByOrderItem = deliveryChallanItems.groupBy { it.id }

            val updatedDeliveryOrderItems = deliveryOrderItems.map { item ->
                val associatedDCs = deliveryChallanItemsGroupedByOrderItem[item.id] ?: emptyList()
                item.copy(associatedDeliveryChallanItems = associatedDCs)
            }

            return deliveryOrder.copy(
                deliveryOrderSections = deliveryOrderSections,
                grandTotalQuantity = grandTotalQuantity,
               grandTotalDeliveredQuantity = grandTotalDeliveredQuantity,
            ).copy(deliveryOrderSections = deliveryOrderSections.map { it.copy(deliveryOrderItems = updatedDeliveryOrderItems.filter {item -> it.deliveryOrderItems.any { it.id == item.id }})} )
        }catch (e: Exception){
            throw e;
        }
    }

    fun calculateDeliveredQuantities(item: DeliveryOrderItem) {
        val challanItemsSql = """
            SELECT
                dc.status,
                dci.deliveringquantity
            FROM deliverychallanitems as dci
            JOIN deliverychallan as dc ON dci.dc_number = dc.dc_number
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

        var totalDeliveredQuantity = 0.0

        deliveryChallanItems.forEach {
              totalDeliveredQuantity += it.deliveringQuantity
        }

        item.deliveredQuantity = totalDeliveredQuantity
    }

    fun findAll(input: ListDeliveryOrderInput): List<DeliveryOrderRecord> {
        val limit = input.size
        val offset = (input.page - 1) * input.size

        val conditions = mutableListOf<String>()
        val params = mutableListOf<Any>()

        // 1. Search by DO Number
        input.search?.let {
            conditions.add("d.do_number ILIKE ?")
            params.add("%$it%")
        }

        // 2. Filter by Statuses
        if (input.statuses.isNotEmpty()) {
            conditions.add("d.status IN (${input.statuses.joinToString { "?" }})")
            params.addAll(input.statuses)
        }

        // 3. Filter by Party IDs
        if (input.partyIds.isNotEmpty()) {
            conditions.add("d.partyId IN (${input.partyIds.joinToString { "?" }})")
            params.addAll(input.partyIds)
        }

        // 4. Filter by CreatedAt Date Range (comparing as bigint)
        input.fromDate?.let { from ->
            conditions.add("d.dateofcontract >= ?")
            params.add(from) // Keep as bigint (milliseconds)
        }

        input.toDate?.let { to ->
            conditions.add("d.dateofcontract <= ?")
            params.add(to) // Keep as bigint (milliseconds)
        }

        val whereClause = if (conditions.isNotEmpty()) "WHERE " + conditions.joinToString(" AND ") else ""

        val sql = """
        SELECT
            d.contractid,
            d.do_number,
            d.dateofcontract,
            p.name AS partyname,
            d.status,
            d.createdat
        FROM DeliveryOrder d
        LEFT JOIN Party p ON d.partyId = p.id
        $whereClause
        ORDER BY d.createdat DESC
        LIMIT ? OFFSET ?
        
        """.trimIndent()

        params.add(limit)
        params.add(offset)

        return jdbcTemplate.query(sql, { rs, _ ->
            val deliveryOrderId = rs.getString("do_number")
            val deliveryOrderItems = deliveryOrderItemRepository.findByDeliveryOrderId(deliveryOrderId)

            var grandTotalQuantity = 0.0
            var grandTotalDeliveredQuantity = 0.0

            deliveryOrderItems.forEach { item ->
                calculateDeliveredQuantities(item)
                grandTotalQuantity += item.quantity?.toDouble() ?: 0.0
                grandTotalDeliveredQuantity += item.deliveredQuantity ?: 0.0
            }

            DeliveryOrderRecord(
                id = rs.getString("do_number"),
                contractId = rs.getString("contractid"),
                dateOfContract = rs.getString("dateofcontract"),
                partyName = rs.getString("partyname"),
                status = rs.getString("status"),
                grandTotalQuantity = grandTotalQuantity,
                grandTotalDeliveredQuantity = grandTotalDeliveredQuantity,
            )
        }, *params.toTypedArray())
    }

    fun update(deliveryOrder: DeliveryOrder): Int? {
        val sql = """
            UPDATE DeliveryOrder SET
                contractId = ?, partyId = ?, dateOfContract = ?, status = ?,
                createdat = ?, updatedat = ?
            WHERE do_number = ?
        """
        return jdbcTemplate.update(
            sql,
            deliveryOrder.contractId,
            deliveryOrder.partyId,
            deliveryOrder.dateOfContract,
            deliveryOrder.status,
            deliveryOrder.createdat,
            deliveryOrder.updatedat,
            deliveryOrder.id
        )
    }
}
