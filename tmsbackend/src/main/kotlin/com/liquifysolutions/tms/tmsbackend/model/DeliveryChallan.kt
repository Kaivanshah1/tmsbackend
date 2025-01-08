package com.liquifysolutions.tms.tmsbackend.model

import java.util.UUID

data class DeliveryChallan(
    val id: String?,
    val do_number: String,
    val dateOfChallan: Long? = null,
    val status: String? = null,
    val partyName: String? = null,
    val totalDeliveringQuantity: Double = 0.0,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val deliveryChallanItems: List<DeliveryChallanItem> = emptyList()
)

data class DeliveryChallanItem(
    val id: String? = UUID.randomUUID().toString(),
    val dc_number: String? = null,
    val deliveryOrderItemId: String? = null,
    val district: String?,
    val taluka: String?,
    val locationName: String? = null,
    val materialName: String? = null,
    val quantity: Double = 0.0,
    val rate: Double = 0.0,
    val dueDate: Long? = null,
    val deliveringQuantity: Double = 0.0
)

data class ListDeliveryChallansInput(
    val search: String? = null,
    val page: Int? = 0,
    val size: Int? = 10,
    val deliveryOrderIds: List<String>? = null
)