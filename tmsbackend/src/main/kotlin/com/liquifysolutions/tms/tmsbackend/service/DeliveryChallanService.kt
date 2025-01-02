package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.DeliveryChallan
import com.liquifysolutions.tms.tmsbackend.model.DeliveryChallanItem
import com.liquifysolutions.tms.tmsbackend.repository.DeliveryChallanRepository
import com.liquifysolutions.tms.tmsbackend.repository.DeliveryOrderItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class DeliveryChallanService(
    private val deliveryChallanRepository: DeliveryChallanRepository,
    private val deliveryOrderItemRepository: DeliveryOrderItemRepository
) {
    fun getAllDeliveryChallans(): List<DeliveryChallan> {
        return deliveryChallanRepository.listAll()
    }

    fun getDeliveryChallanById(id: String): DeliveryChallan? {
        return deliveryChallanRepository.getById(id)
    }

    fun createDeliveryChallan(deliveryOrderId: String): DeliveryChallan? {
        val deliverChallanToCreate = DeliveryChallan(
            id = UUID.randomUUID().toString(),
            deliveryOrderId = deliveryOrderId,
            status = "pending",
            createdAt = Instant.now().toEpochMilli(),
            updatedAt = Instant.now().toEpochMilli()
        )
        val deliveryChallan = deliveryChallanRepository.create(deliverChallanToCreate);
        return deliveryChallanRepository.getById(deliverChallanToCreate.id!!)
    }

    @Transactional
    fun updateDeliveryChallan(deliveryChallan: DeliveryChallan): DeliveryChallan? {
        val existingChallan = deliveryChallanRepository.getById(deliveryChallan.id!!)
        if(existingChallan == null){
            throw  RuntimeException("Delivery Challan not found with id ${deliveryChallan.id}")
        }
        val updatedChallan = deliveryChallan.copy(updatedAt = Instant.now().toEpochMilli()) // Correct way to update updatedAt
        val updatedRows =  deliveryChallanRepository.updateWithItems(updatedChallan)
        if(updatedRows <= 0){
            throw  RuntimeException("Error updating delivery challan with id ${deliveryChallan.id}")
        }
        return deliveryChallanRepository.getById(deliveryChallan.id)
    }


    fun findDeliveryChallanById(id:String) : DeliveryChallan?{
        return deliveryChallanRepository.getById(id)
    }
}