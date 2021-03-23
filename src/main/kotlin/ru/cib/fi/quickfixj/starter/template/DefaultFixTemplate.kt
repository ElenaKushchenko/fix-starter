package ru.cib.fi.quickfixj.starter.template

import quickfix.FieldNotFound
import quickfix.FixVersions
import quickfix.Message
import quickfix.MessageUtils
import quickfix.Session
import quickfix.SessionID
import quickfix.field.ApplVerID
import quickfix.field.BeginString
import quickfix.field.SenderCompID
import quickfix.field.TargetCompID
import ru.cib.fi.quickfixj.starter.exception.FieldNotFoundException
import ru.cib.fi.quickfixj.starter.exception.MessageValidationException
import ru.cib.fi.quickfixj.starter.exception.SessionNotFoundException

class DefaultFixTemplate(
    private val sessionHandler: SessionHandler,
    private val validate: Boolean
): FixTemplate {

    override fun send(message: Message): Boolean {
        val sessionID = SessionIDBuilder(message).build()
        return doSend(message, sessionID)
    }

    override fun send(
        message: Message,
        senderCompID: String,
        targetCompID: String,
        qualifier: String?
    ): Boolean {
        val sessionID = SessionIDBuilder(message)
            .senderCompID(senderCompID)
            .targetCompID(targetCompID)
            .qualifier(qualifier)
            .build()
        return doSend(message, sessionID)
    }

    override fun send(message: Message, sessionID: SessionID): Boolean {
        return doSend(message, sessionID)
    }

    protected fun doSend(message: Message, sessionID: SessionID): Boolean {
        val session: Session =
            sessionHandler.getSession(sessionID) ?: throw SessionNotFoundException("Session not found: $sessionID")
        if (validate) {
            validateMessage(message, session)
        }
        return session.send(message)
    }

    private fun validateMessage(message: Message, session: Session) {
        val dataDictionaryProvider = session.dataDictionaryProvider
        if (dataDictionaryProvider != null) {
            try {
                val applVerID = getApplicationVersionID(message, session)
                val applicationDataDictionary = dataDictionaryProvider.getApplicationDataDictionary(applVerID)
                applicationDataDictionary.validate(message, true)
            } catch (e: Exception) {
                throw MessageValidationException("Message failed validation: ${e.message}", e)
            }
        }
    }

    private fun getApplicationVersionID(
        message: Message,
        session: Session
    ): ApplVerID {
        // If no header return default appl version id
        message.header ?: return getDefaultApplVerID(session)
        return try {
            ApplVerID(message.header.getString(ApplVerID.FIELD))
        } catch (fieldNotFound: FieldNotFound) {
            getDefaultApplVerID(session)
        }
    }

    private fun getDefaultApplVerID(session: Session): ApplVerID {
        val beginString = session.sessionID.beginString
        return if (FixVersions.BEGINSTRING_FIXT11 == beginString) {
            session.senderDefaultApplicationVersionID
        } else {
            MessageUtils.toApplVerID(beginString)
        }
    }

    internal data class SessionIDBuilder(
        val message: Message,
        var beginString: String? = null,
        var senderCompID: String? = null,
        var targetCompID: String? = null,
        var qualifier: String? = null
    ) {

        fun beginString(beginString: String) = apply { this.beginString = beginString }
        fun senderCompID(senderCompID: String) = apply { this.senderCompID = senderCompID }
        fun targetCompID(targetCompID: String) = apply { this.targetCompID = targetCompID }
        fun qualifier(qualifier: String?) = apply { this.qualifier = qualifier }

        fun build(): SessionID {
            if (beginString == null) {
                beginString = getFieldFromMessageHeader(message, BeginString.FIELD)
            }
            if (senderCompID == null) {
                senderCompID =
                    getFieldFromMessageHeader(message, SenderCompID.FIELD)
            }
            if (targetCompID == null) {
                targetCompID =
                    getFieldFromMessageHeader(message, TargetCompID.FIELD)
            }
            if (qualifier == null) {
                qualifier = SessionID.NOT_SET
            }
            return SessionID(beginString, senderCompID, targetCompID, qualifier)
        }

        companion object {
            private fun getFieldFromMessageHeader(message: Message, fieldTag: Int): String {
                return try {
                    message.header.getString(fieldTag)
                } catch (fieldNotFound: FieldNotFound) {
                    throw FieldNotFoundException("Field with ID $fieldTag not found in message", fieldNotFound)
                }
            }
        }
    }

}
