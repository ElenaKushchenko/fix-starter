package ru.cib.fi.quickfixj.starter.connector

import org.springframework.beans.factory.DisposableBean
import quickfix.Connector
import quickfix.SessionID
import java.lang.Exception

class FixConnectionManager: DisposableBean {

    private val sessionToConnector = mutableMapOf<SessionID, Connector>()

    @Synchronized
    fun start(session: SessionID) {
        try {
            sessionToConnector[session]?.stop()
        } catch (ex: Exception) {
            throw IllegalStateException("Could not start the connector", ex)
        }
    }

    @Synchronized
    fun stop(session: SessionID) {
        try {
            sessionToConnector[session]?.stop()
        } catch (ex: Exception) {
            throw IllegalStateException("Could not stop the connector", ex)
        }
    }

    @Synchronized
    fun start() {
        sessionToConnector.keys.forEach { start(it) }
    }

    @Synchronized
    fun stop() {
        sessionToConnector.keys.forEach { stop(it) }
    }

    @Synchronized
    fun addSession(session: SessionID, connector: Connector) {
        sessionToConnector.put(session, connector)
    }

    override fun destroy() {
        stop()
    }
}
