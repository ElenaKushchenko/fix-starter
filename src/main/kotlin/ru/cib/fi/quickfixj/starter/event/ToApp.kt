package ru.cib.fi.quickfixj.starter.event

import org.springframework.context.ApplicationEvent
import quickfix.Message
import quickfix.SessionID

data class ToApp(
    val message: Message,
    val sessionId: SessionID
) : ApplicationEvent(sessionId)