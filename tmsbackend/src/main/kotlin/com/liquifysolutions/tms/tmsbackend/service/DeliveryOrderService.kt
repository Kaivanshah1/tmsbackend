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

        deliveryOrderRepository.create(deliveryOrder)

        var itemsToSave: List<DeliveryOrderItem> = emptyList()

        for (section in sections) {
            for(item in section.deliveryOrderItems) {
                itemsToSave = itemsToSave + item.copy(deliveryOrderId = deliveryOrder.id)
            }
        }

        deliveryOrderItemRepository.saveAll(itemsToSave, deliveryOrder.id);
        return deliveryOrderRepository.findById(deliveryOrder.id);
    }

    fun listAllDeliveryOrder(page: Int, size: Int): List<ListDeliveryOrderItem> {
        val offset = (page - 1) * size
        return deliveryOrderRepository.findAll(size, offset)
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
                item.copy(deliveryOrderId = deliveryOrder.id)
            } ?: emptyList()
        }

        deliveryOrderItemRepository.syncItems(itemsToSave, deliveryOrder.id)
        return deliveryOrderRepository.findById(deliveryOrder.id);
    }

    fun deleteDeliveryOrderById(id: String): Int {
        return deliveryOrderRepository.deleteById(id)
    }
}