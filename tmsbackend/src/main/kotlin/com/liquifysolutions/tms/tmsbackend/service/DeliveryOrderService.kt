package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.*
import com.liquifysolutions.tms.tmsbackend.repository.DeliveryOrderItemRepository
import com.liquifysolutions.tms.tmsbackend.repository.DeliveryOrderRepository
import org.springframework.stereotype.Service

@Service
class DeliveryOrderService(
    private val deliveryOrderRepository: DeliveryOrderRepository,
    private val deliveryOrderItemRepository: DeliveryOrderItemRepository
) {
    fun createDeliveryOrder(deliveryOrder: DeliveryOrder, sections: List<DeliveryOrderSection>): DeliveryOrder? {
        try {
            // 1. Generate the do_number
            val nextDoNumber = generateNextDoNumber()
            val deliveryOrderWithDoNumber = deliveryOrder.copy(id = nextDoNumber)

            // 2. Create the Delivery Order
            deliveryOrderRepository.create(deliveryOrderWithDoNumber)

            // 3. Prepare Delivery Order Items
            var itemsToSave: List<DeliveryOrderItem> = emptyList()

            for (section in sections) {
                for (item in section.deliveryOrderItems) {
                    itemsToSave = itemsToSave + item.copy(do_number = deliveryOrderWithDoNumber.id)
                }
            }

            // 4. Save Delivery Order Items
            deliveryOrderItemRepository.saveAll(itemsToSave, deliveryOrderWithDoNumber.id!!);

            // 5. Return the created Delivery Order with the generated do_number
            return deliveryOrderRepository.findById(deliveryOrderWithDoNumber.id!!);
        }catch (e: Exception){
            throw e;
        }
    }

    fun generateNextDoNumber(): String {
        val lastDoNumber = deliveryOrderRepository.getLastDoNumber()
        val nextNumber = if (lastDoNumber == null) {
            1
        } else {
            val lastNumber = lastDoNumber.substring(3).toIntOrNull() ?: 0
            lastNumber + 1
        }
        return "DO_" + String.format("%04d", nextNumber)
    }


    fun listAllDeliveryOrder(request: ListDeliveryOrderInput): List<DeliveryOrderRecord> {
        return deliveryOrderRepository.findAll(request)
    }

    fun listAllDeliveryOrderItems(deliveryOrderId: String): List<DeliveryOrderItemMetaData>{
        return deliveryOrderRepository.getDeliveryOrderItemById(deliveryOrderId)
    }

    fun getDeliverOrderById(id: String): DeliveryOrder?{
        return deliveryOrderRepository.findById(id)
    }

    fun updateDeliveryOrder(deliveryOrder: DeliveryOrder, sections: List<DeliveryOrderSection>): DeliveryOrder? {
        deliveryOrderRepository.update(deliveryOrder)
        val itemsToSave = sections.flatMap { section ->
            section.deliveryOrderItems?.map { item ->
                item.copy(do_number = deliveryOrder.id)
            } ?: emptyList()
        }

        deliveryOrderItemRepository.syncItems(itemsToSave, deliveryOrder.id!!)
        return deliveryOrderRepository.findById(deliveryOrder.id!!);
    }

}