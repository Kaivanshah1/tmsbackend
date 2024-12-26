package com.liquifysolutions.tms.tmsbackend.controller

import com.liquifysolutions.tms.tmsbackend.model.Party
import com.liquifysolutions.tms.tmsbackend.service.PartyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/api/parties")
class PartyController(private val partyService: PartyService) {

    @GetMapping
    fun getAllParties(): ResponseEntity<List<Party>> =
        ResponseEntity.ok(partyService.getAllParties())

    @GetMapping("/{id}")
    fun getPartyById(@PathVariable id: String): ResponseEntity<Party> =
        partyService.getPartyById(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun createParty(@RequestBody party: Party): ResponseEntity<String> {
        partyService.createParty(party)
        return ResponseEntity.ok("Party created successfully")
    }

    @PutMapping("/{id}")
    fun updateParty(@PathVariable id: String, @RequestBody party: Party): ResponseEntity<String> {
        partyService.updateParty(party.copy(p_id = id))
        return ResponseEntity.ok("Party updated successfully")
    }

    @DeleteMapping("/{id}")
    fun deletePartyById(@PathVariable id: String): ResponseEntity<String> {
        partyService.deletePartyById(id)
        return ResponseEntity.ok("Party deleted successfully")
    }
}