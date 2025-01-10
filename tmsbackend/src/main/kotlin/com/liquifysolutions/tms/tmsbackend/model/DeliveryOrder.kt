package com.liquifysolutions.tms.tmsbackend.model

import java.util.UUID

// input: create, update
// output: get
data class DeliveryOrder(
    val id: String?,
    val contractId: String?,
    val partyId: String?,
    val dateOfContract: Long?,
    val status: String?,
    val grandTotalQuantity: Double = 0.0,
    val grandTotalDeliveredQuantity: Double = 0.0,
    val deliveryOrderSections: List<DeliveryOrderSection>?,
    val createdat: Long? = System.currentTimeMillis(),
    val updatedat: Long? = System.currentTimeMillis()
)

data class AssociatedDeliverChallanItemMetadata(
    val id: String,
    val deliveringQuantity: Double = 0.0,
    val deliveryChallanId: String
)

data class DeliveryOrderItem(
    val id: String = UUID.randomUUID().toString(),
    val do_number: String?,
    val district: String?,
    val taluka: String?,
    val locationId: String?,
    val materialId: String?,
    val quantity: Double? = 0.0,
    var deliveredQuantity: Double? = 0.0,
    val rate: Double?,
    val unit: String?,
    val dueDate: Long?,
    val associatedDeliveryChallanItems: List<AssociatedDeliverChallanItemMetadata> = emptyList(),
)

// List Item for Delivery Challan
data class DeliveryOrderItemMetaData(
    val id: String,
    val district: String?,
    val taluka: String?,
    val locationName: String?,
    val materialName: String?,
    val quantity: Int,
    val dueDate: Long?,
    val rate: Double,
    val status: String = "pending",
    var deliveredQuantity: Double? = 0.0,
)

data class DeliveryOrderSection(
    val district: String?,
    val totalQuantity: Double = 0.0,
    val totalPendingQuantity: Double = 0.0,
    val totalDeliveredQuantity: Double = 0.0,
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


// input: none
// output: List List<DeliveryOrderRecord>
data class DeliveryOrderRecord(
    val id: String,
    val contractId: String?,
    val partyName: String?,
    val status: String?,
    val dateOfContract: String?,
    val grandTotalDeliveredQuantity: Double = 0.0,
    val grandTotalQuantity: Double=0.0,
)

// input: List
data class ListDeliveryOrderInput(
    val search: String? = null,
    val page: Int,
    val size: Int,
    val statuses: List<String> = emptyList(),
    val partyIds: List<String> = emptyList(),
    val fromDate: Long? = null,
    val toDate:Long?=null,
)