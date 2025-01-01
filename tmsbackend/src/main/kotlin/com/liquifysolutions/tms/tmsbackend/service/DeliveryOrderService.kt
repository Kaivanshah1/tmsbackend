package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.DeliveryOrder
import com.liquifysolutions.tms.tmsbackend.model.DeliveryOrderItem
import com.liquifysolutions.tms.tmsbackend.model.DeliveryOrderSection
import com.liquifysolutions.tms.tmsbackend.model.ListDeliveryOrderItem
import com.liquifysolutions.tms.tmsbackend.repository.DeliveryOrderItemRepository
import com.liquifysolutions.tms.tmsbackend.repository.DeliveryOrderRepository
import org.springframework.stereotype.Service

@Service
class DeliveryOrderService(
    private val deliveryOrderRepository: DeliveryOrderRepository,
    private val deliveryOrderItemRepository: DeliveryOrderItemRepository
) {
    fun createDeliveryOrder(deliveryOrder: DeliveryOrder, sections: List<DeliveryOrderSection>): Int{

        deliveryOrderRepository.create(deliveryOrder)

        var itemsToSave: List<DeliveryOrderItem> = emptyList()

        for (section in sections) {
            for(item in section.deliveryOrderItems) {
                itemsToSave = itemsToSave + item.copy(deliveryOrderId = deliveryOrder.id)
            }
        }

        deliveryOrderItemRepository.saveAll(itemsToSave, deliveryOrder.id);
        return 1
    }

    fun listAllDeliveryOrder(page: Int, size: Int): List<ListDeliveryOrderItem> {
        val offset = (page - 1) * size
        return deliveryOrderRepository.findAll(size, offset)
    }

    fun getDeliverOrderById(id: String): DeliveryOrder?{
        return deliveryOrderRepository.findById(id)
    }

    fun updateDeliveryOrder(deliveryOrder: DeliveryOrder, sections: List<DeliveryOrderSection>): Int {
        deliveryOrderRepository.update(deliveryOrder)
        val itemsToSave = sections.flatMap { section ->
            section.deliveryOrderItems?.map { item ->
                item.copy(deliveryOrderId = deliveryOrder.id)
            } ?: emptyList()
        }

        deliveryOrderItemRepository.syncItems(itemsToSave, deliveryOrder.id)
        return 1;
    }

    fun deleteDeliveryOrderById(id: String): Int {
        return deliveryOrderRepository.deleteById(id)
    }
}