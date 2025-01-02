package com.liquifysolutions.tms.tmsbackend.model

import java.util.UUID

data class DeliveryChallan (
    val id: String? = UUID.randomUUID().toString(),
    val status: String?,
    val do_id: String,
    val date: Long?,
    val dc_items: List<DeliveryChallanItems> = emptyList()
)

data class DeliveryChallanItems(
    val id: String?,
    val district: String?,
    val taluka: String?,
    val location: String?,
    val weight: Double?,
    val do_id: String?,
    val expectedQuantity: Double?
)


