package com.liquifysolutions.tms.tmsbackend.controller

import com.liquifysolutions.tms.tmsbackend.model.ListPartiesInput
import com.liquifysolutions.tms.tmsbackend.model.Party
import com.liquifysolutions.tms.tmsbackend.service.PartyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/api/v1/parties")
class PartyController(private val partyService: PartyService) {

    @PostMapping("/list")
    fun getAllParties(@RequestBody request: ListPartiesInput): ResponseEntity<Any> {
        val parties = partyService.searchParties(
            search = request.search,
            statuses = request.statuses,
            getAll = request.getAll,
            page = request.page,
            size = request.size
        )
        return ResponseEntity.ok(parties)
    }

    @GetMapping("/activate/{partyId}")
    fun activateParty(@PathVariable partyId: String): ResponseEntity<Void> {
        partyService.activateParty(partyId)
        return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/deactivate/{partyId}")
    fun deactivateParty(@PathVariable partyId: String): ResponseEntity<Void> {
        partyService.deactivateParty(partyId)
        return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/get/{id}")
    fun getPartyById(@PathVariable id: String): ResponseEntity<Party> =
        partyService.getPartyById(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping("/create")
    fun createParty(@RequestBody party: Party): ResponseEntity<String> {
        partyService.createParty(party)
        return ResponseEntity.ok("Party created successfully")
    }

    @PostMapping("/update")
    fun updateParty(@RequestBody party: Party): ResponseEntity<String> {
        partyService.updateParty(party)
        return ResponseEntity.ok("Party updated successfully")
    }

    @DeleteMapping("/{id}")
    fun deletePartyById(@PathVariable id: String): ResponseEntity<String> {
        partyService.deletePartyById(id)
        return ResponseEntity.ok("Party deleted successfully")
    }
}