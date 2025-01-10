package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.*
import com.liquifysolutions.tms.tmsbackend.repository.DeliveryChallanRepository
import com.liquifysolutions.tms.tmsbackend.repository.DeliveryOrderItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class DeliveryChallanService(
    private val deliveryChallanRepository: DeliveryChallanRepository,
    private val deliveryOrderItemRepository: DeliveryOrderItemRepository,

) {
    fun getAllDeliveryChallans(request: ListDeliveryChallansInput): List<DeliveryChallanOutputRecord>{
        return deliveryChallanRepository.listAll(request)
    }

    fun getDeliveryChallanById(id: String): DeliveryChallan? {
        return deliveryChallanRepository.getById(id)
    }

    fun generateDeliveryChallanId(): String {
        val rowCount = deliveryChallanRepository.getDeliveryChallanCount()
        val nextId = rowCount?.plus(1)
        return String.format("DC_%04d", nextId)
    }

    fun createDeliveryChallan(deliveryOrderId: String): DeliveryChallan? {
        val deliveryChallanToCreate = DeliveryChallan(
            id = generateDeliveryChallanId(),
            deliveryOrderId = deliveryOrderId,
            status = "pending",
            totalDeliveringQuantity = 0.0,
            createdAt = Instant.now().toEpochMilli(),
            updatedAt = Instant.now().toEpochMilli(),
//            transportationCompanyId = null,
//            vehicleId = null,
//            driverId = null
        )
        deliveryChallanRepository.create(deliveryChallanToCreate)
        return deliveryChallanRepository.getById(deliveryChallanToCreate.id!!)
    }

    @Transactional
    fun updateDeliveryChallan(deliveryChallan: DeliveryChallan): DeliveryChallan? {
        if(deliveryChallan.id == null){
            throw IllegalArgumentException("Delivery Challan Id not found");
        }

        val existingChallan = deliveryChallanRepository.getById(deliveryChallan.id)
        if (existingChallan == null) {
            throw IllegalArgumentException("Delivery challan not found")
        }

        val deliveryChallanToUpdate = deliveryChallan.copy(
            deliveryChallanItems = deliveryChallan.deliveryChallanItems.map {
                it.takeUnless { it.id == null } ?: it.copy(id = UUID.randomUUID().toString())
            },
            updatedAt = Instant.now().epochSecond
        )

        return deliveryChallanRepository.update(deliveryChallanToUpdate)
    }

    fun findDeliveryChallanById(id:String) : DeliveryChallan?{
        return deliveryChallanRepository.getById(id)
    }
}