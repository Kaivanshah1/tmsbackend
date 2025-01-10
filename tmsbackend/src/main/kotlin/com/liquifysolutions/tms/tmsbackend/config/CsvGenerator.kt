package com.liquifysolutions.tms.tmsbackend.config

import com.liquifysolutions.tms.tmsbackend.model.DeliveryOrder
import com.opencsv.CSVWriter
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

@Component
class CsvGenerator {
    private val dateFormat = SimpleDateFormat("dd-MMM").apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun generateCsv(order: DeliveryOrder): ByteArray {
        return ByteArrayOutputStream().use { byteStream ->
            OutputStreamWriter(byteStream).use { writer ->
                CSVWriter(writer).use { csvWriter ->
                    generateHeaderSection(csvWriter, order)
                    generateItemsSection(csvWriter, order)
                    generateDCSection(csvWriter, order)
                }
            }
            byteStream.toByteArray()
        }
    }

    private fun generateHeaderSection(csvWriter: CSVWriter, order: DeliveryOrder) {
        csvWriter.writeNext(arrayOf("DO Number", "", order.id ?: ""))
        csvWriter.writeNext(arrayOf("Total Quantity", "", order.grandTotalQuantity.toString()))
        csvWriter.writeNext(arrayOf("Total Delivered", "", order.grandTotalDeliveredQuantity.toString()))
        csvWriter.writeNext(arrayOf("Client Contact Number", "", "ABC 123"))  // You might want to get this from order
        csvWriter.writeNext(arrayOf("Party", "", order.partyId ?: ""))
        csvWriter.writeNext(arrayOf("Date Of Contract", "", order.dateOfContract?.let { dateFormat.format(Date(it)) } ?: ""))
        csvWriter.writeNext(arrayOf(""))  // Empty line
        csvWriter.writeNext(arrayOf("Delivery Order Items"))
        csvWriter.writeNext(arrayOf(""))  // Empty line

        // Header for items
        csvWriter.writeNext(arrayOf(
            "Sr No",
            "Taluka",
            "Location",
            "Material",
            "Quantity",
            "Delivered Quantity",
            "Rate",
            "Status",
            "DC"
        ))
    }

    private fun generateItemsSection(csvWriter: CSVWriter, order: DeliveryOrder) {
        order.deliveryOrderSections?.forEachIndexed { sectionIndex, section ->
            var srNo = 1
            section.deliveryOrderItems.forEach { item ->
                csvWriter.writeNext(arrayOf(
                    srNo++.toString(),
                    item.taluka ?: "",
                    item.locationId ?: "",
                    item.materialId ?: "",
                    item.quantity?.toString() ?: "0",
                    item.deliveredQuantity?.toString() ?: "0",
                    item.rate?.toString() ?: "0",
                    order.status ?: "Pending",
                    item.do_number ?: ""
                ))
            }

            // District total
            csvWriter.writeNext(arrayOf(
                "Total For District:",
                "",
                "",
                "",
                section.totalQuantity.toString(),
                section.totalDeliveredQuantity.toString()
            ))
            csvWriter.writeNext(arrayOf("District ${sectionIndex + 1}", section.district ?: ""))
            csvWriter.writeNext(arrayOf(""))  // Empty line
        }
    }

    private fun generateDCSection(csvWriter: CSVWriter, order: DeliveryOrder) {
        csvWriter.writeNext(arrayOf(""))  // Empty line
        csvWriter.writeNext(arrayOf("Delivery Challan"))
        csvWriter.writeNext(arrayOf("Sr No", "Id", "Date", "Quantity"))

        // Here you would need to add actual DC data if available in your model
        // For now, leaving it empty or you can modify the model to include DC information
    }
}
