package com.liquifysolutions.tms.tmsbackend.controller

import com.liquifysolutions.tms.tmsbackend.model.Material
import com.liquifysolutions.tms.tmsbackend.service.MaterialService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/api/v1/materials")
class MaterialController(private val materialService: MaterialService) {

    @PostMapping("/create")
    fun createMaterial(@RequestBody material: Material): ResponseEntity<String> {
        materialService.create(material)
        return ResponseEntity.ok("Material created successfully.")
    }

    @GetMapping("/get/{id}")
    fun getMaterialById(@PathVariable id: String): ResponseEntity<Material> {
        val material = materialService.findById(id)
        return if (material != null) {
            ResponseEntity.ok(material)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/list")
    fun getAllMaterials(): ResponseEntity<List<Material>> {
        val materials = materialService.findAll()
        return ResponseEntity.ok(materials)
    }

    @PostMapping("/update")
    fun updateMaterial(@RequestBody material: Material): ResponseEntity<String> {
        materialService.update(material)
        return ResponseEntity.ok("Material updated successfully.")
    }

    @DeleteMapping("/{id}")
    fun deleteMaterial(@PathVariable id: String): ResponseEntity<String> {
        materialService.deleteById(id)
        return ResponseEntity.ok("Material deleted successfully.")
    }
}