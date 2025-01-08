package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.Party
import com.liquifysolutions.tms.tmsbackend.repository.PartyRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class PartyService(private val partyRepository: PartyRepository) {

    fun searchParties(
        search: String?,
        statuses: List<String>?,
        getAll: Boolean?,
        page: Int?,
        size: Int?
    ): Any {
        return partyRepository.findParties(search, statuses, page, size, getAll)
    }

    @Transactional
    fun deactivateParty(partyId: String) {
        val party = partyRepository.getPartyById(partyId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Party not found with id $partyId")
        val rowsUpdated = partyRepository.deactivateParty(partyId)
        if(rowsUpdated == 0){
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to deactivate the party")
        }
    }

    @Transactional
    fun activateParty(partyId: String) {
        val party = partyRepository.getPartyById(partyId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Party not found with id $partyId")
        val rowsUpdated = partyRepository.activateParty(partyId)
        if(rowsUpdated == 0){
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to activate the party")
        }
    }
    fun getPartyById(id: String): Party? = partyRepository.getPartyById(id)

    fun createParty(party: Party) = partyRepository.createParty(party)

    fun updateParty(party: Party): Int = partyRepository.updateParty(party)

    fun deletePartyById(id: String): Int = partyRepository.deletePartyById(id)
}