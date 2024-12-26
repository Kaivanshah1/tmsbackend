package com.liquifysolutions.tms.tmsbackend.service

import com.liquifysolutions.tms.tmsbackend.model.Party
import com.liquifysolutions.tms.tmsbackend.repository.PartyRepository
import org.springframework.stereotype.Service

@Service
class PartyService(private val partyRepository: PartyRepository) {

    fun getAllParties(): List<Party> = partyRepository.getAllParties()

    fun getPartyById(id: String): Party? = partyRepository.getPartyById(id)

    fun createParty(party: Party): Int = partyRepository.createParty(party)

    fun updateParty(party: Party): Int = partyRepository.updateParty(party)

    fun deletePartyById(id: String): Int = partyRepository.deletePartyById(id)
}