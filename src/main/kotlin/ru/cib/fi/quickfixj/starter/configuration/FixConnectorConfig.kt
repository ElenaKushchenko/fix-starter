package ru.cib.fi.quickfixj.starter.configuration

data class FixConnectorConfig (
    val autoStartup: Boolean,
    val configs: List<String>
)
