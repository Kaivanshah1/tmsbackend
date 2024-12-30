package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.Material
import com.liquifysolutions.tms.tmsbackend.repository.MaterialRepository
import org.springframework.stereotype.Service

@Service
class MaterialService(private val materialRepository: MaterialRepository) {

    fun create(material: Material): Int = materialRepository.create(material)

    fun findById(id: String): Material? = materialRepository.findById(id)

    fun findAll(): List<Material> = materialRepository.findAll()

    fun update(material: Material): Int = materialRepository.update(material)

    fun deleteById(id: String): Int = materialRepository.deleteById(id)
}