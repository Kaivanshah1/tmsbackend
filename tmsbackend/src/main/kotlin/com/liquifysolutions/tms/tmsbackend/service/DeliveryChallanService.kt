package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.DeliveryChallan
import com.liquifysolutions.tms.tmsbackend.repository.DeliveryChallanRepository
import org.springframework.stereotype.Service

@Service
class DeliveryChallanService(private val deliveryChallanRepository: DeliveryChallanRepository) {
    fun getAllDeliveryChallans(): List<DeliveryChallan> {
        return deliveryChallanRepository.listAll()
    }

    fun getDeliveryChallanById(id: String): DeliveryChallan? {
        return deliveryChallanRepository.getById(id)
    }

    fun createDeliveryChallan(do_id: String): Int {
        return deliveryChallanRepository.create(do_id)
    }
}