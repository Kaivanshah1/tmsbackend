package com.liquifysolutions.tms.tmsbackend.model

import java.util.UUID

data class DeliveryOrder(
    val id: String = UUID.randomUUID().toString(),
    val contractId: String?,
    val partyId: String?,
    val dateOfContract: Long?,
    val partyname: String?,
    val status: String,
    val deliveryOrderSections: List<DeliveryOrderSection>?,
    val grandTotalQuantity: Int? = 0,
    val grandTotalPendingQuantity: Int? = 0,
    val grandTotalInProgressQuantity: Int? = 0,
    val grandTotalDeliveredQuantity: Int? = 0,
    val createdAt: Long? = System.currentTimeMillis(),
    val updatedAt: Long? = System.currentTimeMillis()
)

data class DeliveryOrderItem(
    val id: String = UUID.randomUUID().toString(),
    val deliveryOrderId: String?,
    val district: String?,
    val taluka: String?,
    val locationId: String?,
    val materialId: String?,
    val quantity: Int,
    var pendingQuantity: Int? = 0,
    var deliveredQuantity: Int? = 0,
    var inProgressQuantity: Int? = 0,
    val rate: Double?,
    val unit: String?,
    val dueDate: Long?,
    val status: String
)

data class DeliveryOrderItemMetaData(
    val id: String,
    val district: String?,
    val taluka: String?,
    val locationName: String?,
    val materialName: String?,
    val quantity: Int,
    val dueDate: Long?,
    val rate: Double,
    val status: String,
    var deliveredQuantity: Double? = 0.0,
    var inProgressQuantity: Double? = 0.0,
)

data class DeliveryOrderSection(
    val district: String?,
    val totalQuantity: Int = 0,
    val totalPendingQuantity: Int = 0,
    val totalInProgressQuantity: Int = 0,
    val totalDeliveredQuantity: Int = 0,
    val status: String,
    var deliveryOrderItems: List<DeliveryOrderItem> = emptyList()
)

data class ListDeliveryOrderItem(
    val id: String?,
    val contractId: String?,
    val partyId: String?,
    val partyname: String?,
    val status: String?,
    val createdAt: Long?,
)