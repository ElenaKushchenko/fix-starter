package ru.cib.fi.quickfixj.starter.template

import quickfix.Session
import quickfix.SessionID

interface SessionHandler {
    fun getSession(sessionID: SessionID): Session?
    fun getSessionState(sessionID: SessionID): SessionState?
}