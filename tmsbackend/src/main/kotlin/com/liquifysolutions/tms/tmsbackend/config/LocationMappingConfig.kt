package com.liquifysolutions.tms.tmsbackend.config
import com.liquifysolutions.tms.tmsbackend.model.State
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "locations")
class LocationMappingConfig {
    var states: List<State> = listOf()
}