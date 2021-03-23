package ru.cib.fi.quickfixj.starter.event

import org.springframework.context.ApplicationEvent
import quickfix.SessionID

data class Logout(val sessionId: SessionID) : ApplicationEvent(sessionId)