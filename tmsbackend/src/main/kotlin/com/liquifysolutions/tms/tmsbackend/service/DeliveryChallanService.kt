package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.DeliveryChallan
import com.liquifysolutions.tms.tmsbackend.model.ListDeliveryChallansInput
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
    fun getAllDeliveryChallans(request: ListDeliveryChallansInput): List<DeliveryChallan>{
        return deliveryChallanRepository.listAll(request)
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