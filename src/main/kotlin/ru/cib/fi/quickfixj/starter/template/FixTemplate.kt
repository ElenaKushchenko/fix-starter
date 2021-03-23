package ru.cib.fi.quickfixj.starter.template

import quickfix.Message
import quickfix.SessionID

interface FixTemplate {

    fun send(message: Message): Boolean

    fun send(message: Message, sessionID: SessionID): Boolean

    fun send(message: Message, senderCompID: String, targetCompID: String, qualifier: String? = null): Boolean
}