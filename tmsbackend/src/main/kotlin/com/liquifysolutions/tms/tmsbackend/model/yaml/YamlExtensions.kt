package com.liquifysolutions.tms.tmsbackend.model.yaml

import com.liquifysolutions.tms.tmsbackend.model.District
import com.liquifysolutions.tms.tmsbackend.model.Taluka
import com.liquifysolutions.tms.tmsbackend.model.City
import com.liquifysolutions.tms.tmsbackend.model.State
import java.util.UUID

fun YamlLocation.toState(): State {
    return State(
        s_id = UUID.randomUUID().toString(),
        name = this.name
    )
}

fun YamlDistrict.toDistrict(): District {
    return District(
        d_id = UUID.randomUUID().toString(),
        name = this.name
    )
}

fun YamlTaluka.toTaluka(districtId: String): Taluka {
    return Taluka(
        t_id = UUID.randomUUID().toString(),
        name = this.name,
        district_id = districtId
    )
}

fun String.toCity(talukaId: String): City {
    return City(
        c_id = UUID.randomUUID().toString(),
        name = this,
        taluka_id = talukaId
    )
}