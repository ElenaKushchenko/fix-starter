package ru.cib.fi.quickfixj.starter.application

import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import quickfix.Application
import quickfix.Message
import quickfix.SessionID
import ru.cib.fi.quickfixj.starter.event.Create
import ru.cib.fi.quickfixj.starter.event.FromAdmin
import ru.cib.fi.quickfixj.starter.event.FromApp
import ru.cib.fi.quickfixj.starter.event.Logon
import ru.cib.fi.quickfixj.starter.event.Logout
import ru.cib.fi.quickfixj.starter.event.ToAdmin
import ru.cib.fi.quickfixj.starter.event.ToApp

class EventApplicationAdapter(private val eventPublisher: ApplicationEventPublisher): Application {
    
    override fun onLogon(sessionId: SessionID) = publishEvent(Logon(sessionId))

    override fun onCreate(sessionId: SessionID) = publishEvent(Create(sessionId))

    override fun onLogout(sessionId: SessionID) = publishEvent(Logout(sessionId))

    override fun toAdmin(message: Message, sessionId: SessionID) = publishEvent(ToAdmin(message, sessionId))

    override fun toApp(message: Message, sessionId: SessionID) = publishEvent(ToApp(message, sessionId))

    override fun fromAdmin(message: Message, sessionId: SessionID) = publishEvent(FromAdmin(message, sessionId))

    override fun fromApp(message: Message, sessionId: SessionID) = publishEvent(FromApp(message, sessionId))
    
    private fun publishEvent(event: ApplicationEvent) = eventPublisher.publishEvent(event)
}