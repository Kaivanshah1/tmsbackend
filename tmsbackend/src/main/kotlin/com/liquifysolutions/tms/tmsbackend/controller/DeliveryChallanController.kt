package com.liquifysolutions.tms.tmsbackend.controller

import com.liquifysolutions.tms.tmsbackend.service.DeliveryChallanService
import com.liquifysolutions.tms.tmsbackend.model.DeliveryChallan
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/api/v1/delivery-challans")
class DeliveryChallanController(
    private val deliveryChallanService: DeliveryChallanService
) {
    @PostMapping("/list")
    fun getAllDeliveryChallans(): List<DeliveryChallan> {
        return deliveryChallanService.getAllDeliveryChallans()
    }

    @GetMapping("/get/{id}")
    fun getDeliveryChallanById(@PathVariable id: String): DeliveryChallan? {
        return deliveryChallanService.getDeliveryChallanById(id)
    }

    @GetMapping("/create/from-delivery-order/{id}")
    fun createDeliveryChallan(@PathVariable id: String): DeliveryChallan? {
        return deliveryChallanService.createDeliveryChallan(id)
    }

    @PostMapping("/update")
    fun updateDeliveryChallan(@RequestBody deliveryChallan: DeliveryChallan): DeliveryChallan? {
        return deliveryChallanService.updateDeliveryChallan(deliveryChallan)
    }
}
