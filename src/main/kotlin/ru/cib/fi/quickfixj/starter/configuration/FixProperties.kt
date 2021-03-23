package ru.cib.fi.quickfixj.starter.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import ru.cib.fi.quickfixj.starter.configuration.FixProperties.Companion.PROPERTY_PREFIX

@ConstructorBinding
@ConfigurationProperties(prefix = PROPERTY_PREFIX)
data class FixProperties (
    val client: FixConnectorConfig,
    val server: FixConnectorConfig
) {

    companion object {
        const val PROPERTY_PREFIX = "quickfixj"
    }
}

