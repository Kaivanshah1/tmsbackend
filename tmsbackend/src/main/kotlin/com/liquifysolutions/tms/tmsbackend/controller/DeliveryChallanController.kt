package com.liquifysolutions.tms.tmsbackend.controller

import com.liquifysolutions.tms.tmsbackend.service.DeliveryChallanService
import com.liquifysolutions.tms.tmsbackend.model.DeliveryChallan
import com.liquifysolutions.tms.tmsbackend.service.DeliveryOrderService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
@RequestMapping("/api/v1/delivery-challan")
class DeliveryChallanController(private val deliveryChallanService: DeliveryChallanService, private val deliveryOrderService: DeliveryOrderService) {
    @PostMapping("/list")
    fun getAllDeliveryChallans(): List<DeliveryChallan> {
        return deliveryChallanService.getAllDeliveryChallans()
    }

    @GetMapping("/get/{id}")
    fun getDeliveryChallanById(@PathVariable id: String): DeliveryChallan? {
        return deliveryChallanService.getDeliveryChallanById(id)
    }

    @PostMapping("/create")
    fun createDeliveryChallan(@RequestBody do_id: String): Int {
        return deliveryChallanService.createDeliveryChallan(do_id);
    }
}