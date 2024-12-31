package com.liquifysolutions.tms.tmsbackend.controller

import com.liquifysolutions.tms.tmsbackend.model.DeliveryOrder
import com.liquifysolutions.tms.tmsbackend.service.DeliveryOrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
@RequestMapping("/api/v1/delivery-orders")
class DeliveryOrderController(
    private val deliveryOrderService: DeliveryOrderService
) {
    @PostMapping("/create")
    fun createDeliveryOrder(@RequestBody request: DeliveryOrder): ResponseEntity<Int> {

        val deliveryOrderSections = request.deliveryOrderSections ?: emptyList();

        // Pass the constructed DeliveryOrder and sections to the service
        val createdDeliveryOrderId = deliveryOrderService.createDeliveryOrder(request, deliveryOrderSections)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDeliveryOrderId)
    }

    @GetMapping("/list")
    fun getAllDeliveryOrder(): ResponseEntity<List<DeliveryOrder>>{
        val getAllDeliveryOrder = deliveryOrderService.listAllDeliveryOrder();
        return ResponseEntity.status(HttpStatus.CREATED).body(getAllDeliveryOrder);
    }

    @GetMapping("/{id}")
    fun getDeliveryOrderById(@PathVariable id: String): ResponseEntity<DeliveryOrder?> {
        val deliveryOrder = deliveryOrderService.listDeliveryOrderById(id)
        return if (deliveryOrder != null) {
            ResponseEntity.ok(deliveryOrder)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/update")
    fun updateDeliveryOrder(@RequestBody deliveryOrder: DeliveryOrder): ResponseEntity<String> {
        return try {
            val rowsUpdated = deliveryOrderService.updateDeliveryOrder(deliveryOrder)
            if (rowsUpdated > 0) {
                ResponseEntity.ok("Delivery order updated successfully.")
            } else {
                ResponseEntity.badRequest().body("Failed to update delivery order.")
            }
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("An error occurred: ${e.message}")
        }
    }

    @DeleteMapping("/delete/{id}")
    fun deleteDeliveryOrder(@PathVariable id: String): ResponseEntity<String> {
        return try {
            val rowsDeleted = deliveryOrderService.deleteDeliveryOrderById(id)
            if (rowsDeleted > 0) {
                ResponseEntity.ok("Delivery order deleted successfully.")
            } else {
                ResponseEntity.badRequest().body("Delivery order not found or could not be deleted.")
            }
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("An error occurred: ${e.message}")
        }
    }
}