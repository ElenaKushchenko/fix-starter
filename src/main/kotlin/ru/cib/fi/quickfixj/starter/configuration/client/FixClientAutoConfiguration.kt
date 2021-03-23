/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.cib.fi.quickfixj.starter.configuration.client

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import quickfix.*
import ru.cib.fi.quickfixj.starter.application.EventApplicationAdapter
import ru.cib.fi.quickfixj.starter.configuration.FixProperties
import ru.cib.fi.quickfixj.starter.connector.FixConnectionManager


@Configuration
@EnableConfigurationProperties(FixProperties::class)
class FixClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = ["clientApplication"])
    fun clientApplication(applicationEventPublisher: ApplicationEventPublisher): Application =
            EventApplicationAdapter(applicationEventPublisher)

    @Bean(name = ["clientMessageStoreFactory"])
    @ConditionalOnMissingBean(name = ["clientMessageStoreFactory"])
    fun clientMemoryStoreFactory(): MessageStoreFactory = MemoryStoreFactory()

    @Bean
    @ConditionalOnMissingBean(name = ["clientMessageFactory"])
    fun clientMessageFactory(): MessageFactory = DefaultMessageFactory()

    @Bean
    fun clientConnectorManager(
            properties: FixProperties,
            clientApplication: Application,
            clientMessageStoreFactory: MessageStoreFactory,
            clientMessageFactory: MessageFactory
    ): FixConnectionManager {
        val connectionManager = FixConnectionManager()
        properties.client.configs.map {
            val sessionSettings = SessionSettings(it)
            val sessionID = sessionSettings.sectionIterator().next()
            val logFactory = FileLogFactory(sessionSettings)
            val connector = ThreadedSocketInitiator.newBuilder()
                    .withApplication(clientApplication)
                    .withMessageStoreFactory(clientMessageStoreFactory)
                    .withSettings(sessionSettings)
                    .withLogFactory(logFactory)
                    .withMessageFactory(clientMessageFactory)
                    .build()
            connectionManager.addSession(sessionID, connector)
        }
        if (properties.client.autoStartup) {
            connectionManager.start()
        }
        return connectionManager
    }

}
