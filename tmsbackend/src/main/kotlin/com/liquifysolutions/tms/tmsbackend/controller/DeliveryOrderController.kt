package com.liquifysolutions.tms.tmsbackend.controller

import com.liquifysolutions.tms.tmsbackend.model.*
import com.liquifysolutions.tms.tmsbackend.service.DeliveryOrderService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
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
    fun createDeliveryOrder(@RequestBody request: DeliveryOrder): ResponseEntity<DeliveryOrder> {
        val deliveryOrderSections = request.deliveryOrderSections ?: emptyList();
        val createdDeliveryOrder = deliveryOrderService.createDeliveryOrder(request, deliveryOrderSections)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDeliveryOrder)
    }

    @PostMapping("/list")
    fun getAllDeliveryOrder(
        @RequestBody request: ListDeliveryOrderInput
    ): ResponseEntity<List<DeliveryOrderRecord>> {
        val deliveryOrders = deliveryOrderService.listAllDeliveryOrder(request)
        return ResponseEntity.status(HttpStatus.OK).body(deliveryOrders)
    }

    @GetMapping("/list/delivery-order-items/{id}")
    fun getAllDeliveryItems(@PathVariable id: String): List<DeliveryOrderItemMetaData>{
        return deliveryOrderService.listAllDeliveryOrderItems(id)
    }

    @GetMapping("/download-csv/{deliveryOrderId}")
    fun downloadDeliveryOrderCsv(@PathVariable deliveryOrderId: String): ResponseEntity<ByteArray> {
        val csvData = deliveryOrderService.generateDeliveryOrderCsv(deliveryOrderId)
        val fileName = "delivery_order_$deliveryOrderId.csv"
        val headers = HttpHeaders().apply {
            contentType = MediaType.parseMediaType("text/csv")
            setContentDisposition(org.springframework.http.ContentDisposition.builder("attachment").filename(fileName).build())
        }

        return ResponseEntity(csvData, headers, HttpStatus.OK)
    }

    @GetMapping("/get/{id}")
    fun getDeliveryOrderById(@PathVariable id: String): ResponseEntity<DeliveryOrder?> {
        val deliveryOrder = deliveryOrderService.getDeliverOrderById(id)
        return if (deliveryOrder != null) {
            ResponseEntity.ok(deliveryOrder)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/update")
    fun updateDeliveryOrder(@RequestBody deliveryOrder: DeliveryOrder): ResponseEntity<DeliveryOrder> {
        val deliveryOrderSections = deliveryOrder.deliveryOrderSections ?: emptyList();
        return try {
            val updatedObject = deliveryOrderService.updateDeliveryOrder(deliveryOrder, deliveryOrderSections)
            ResponseEntity.ok(updatedObject);
        } catch (e: Exception) {
           throw e;
        }
    }
}