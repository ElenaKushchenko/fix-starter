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
package ru.cib.fi.quickfixj.starter.configuration.server

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
class FixServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = ["serverApplication"])
    fun serverApplication(applicationEventPublisher: ApplicationEventPublisher): Application =
            EventApplicationAdapter(applicationEventPublisher)

    @Bean(name = ["serverMessageStoreFactory"])
    @ConditionalOnMissingBean(name = ["serverMessageStoreFactory"])
    fun serverMemoryStoreFactory(): MessageStoreFactory = MemoryStoreFactory()

    @Bean
    @ConditionalOnMissingBean(name = ["serverMessageFactory"])
    fun serverMessageFactory(): MessageFactory = DefaultMessageFactory()

    @Bean
    fun serverConnectorManager(
            properties: FixProperties,
            serverApplication: Application,
            serverMessageStoreFactory: MessageStoreFactory,
            serverMessageFactory: MessageFactory
    ): FixConnectionManager {
        val connectionManager = FixConnectionManager()
        properties.server.configs.map {
            val sessionSettings = SessionSettings(it)
            val sessionID = sessionSettings.sectionIterator().next()
            val logFactory = FileLogFactory(sessionSettings)
            val connector = ThreadedSocketAcceptor.newBuilder()
                    .withApplication(serverApplication)
                    .withMessageStoreFactory(serverMessageStoreFactory)
                    .withSettings(sessionSettings)
                    .withLogFactory(logFactory)
                    .withMessageFactory(serverMessageFactory)
                    .build()
            connectionManager.addSession(sessionID, connector)
        }
        if (properties.server.autoStartup) {
            connectionManager.start()
        }
        return connectionManager
    }

}
