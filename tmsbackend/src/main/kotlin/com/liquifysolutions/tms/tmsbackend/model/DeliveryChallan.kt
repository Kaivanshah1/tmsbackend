package com.liquifysolutions.tms.tmsbackend.model

import java.util.UUID

data class DeliveryChallan(
    val id: String?,
    val deliveryOrderId: String,
    val dateOfChallan: Long? = null,
    val status: String? = null,
    val partyName: String? = null,
    val totalDeliveringQuantity: Double = 0.0,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val deliveryChallanItems: List<DeliveryChallanItem> = emptyList(),
    val transportationCompanyId: String? = "",
    val vehicleId: String? = "",
    val driverId: String? = ""
)

data class DeliveryChallanItem(
    val id: String? = UUID.randomUUID().toString(),
    val deliveryChallanId : String? = null,
    val deliveryOrderItemId: String? = null,
    val district: String?,
    val taluka: String?,
    val locationId: String? = null,
    val materialId: String? = null,
    val quantity: Double = 0.0,
    val rate: Double = 0.0,
    val dueDate: Long? = null,
    val deliveringQuantity: Double = 0.0,
    val deliveredQuantity: Double = 0.0
)

data class ListDeliveryChallansInput(
    val search: String? = null,
    val page: Int? = 0,
    val size: Int? = 10,
    val toDate: Long?,
    val fromDate: Long?,
    val statuses: List<String>? = emptyList(),
    val getAll: Boolean,
    val partyIds: List<String> = emptyList(),
    val transportationCompanyIds: List<String> = emptyList(),
    val deliveryOrderIds: List<String>? = emptyList()
)

data class DeliveryChallanOutputRecord(
    val id: String,
    val deliveryOrderId: String,
    val dateOfChallan: Long?,
    val status: String?,
    val partyName: String?,
    val transportationCompanyName: String?,
    val driverName: String?,
    val totalDeliveringQuantity: Double = 0.0,
)