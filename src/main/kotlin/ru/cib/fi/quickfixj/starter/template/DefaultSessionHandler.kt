package ru.cib.fi.quickfixj.starter.template

import quickfix.Session
import quickfix.SessionID

class DefaultSessionHandler: SessionHandler {
    
    override fun getSession(sessionID: SessionID): Session? = Session.lookupSession(sessionID)

    override fun getSessionState(sessionID: SessionID): SessionState? =
        Session.lookupSession(sessionID)?.let { SessionState.resolveState(it) }
}