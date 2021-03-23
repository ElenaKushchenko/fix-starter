package ru.cib.fi.quickfixj.starter.template

import quickfix.Session

enum class SessionState {
    Enabled,
    LoggedOn,
    LogonAlreadySent,
    LogonReceived,
    LogonSentNeeded,
    LogonSent,
    LogonTimeout,
    LogoutSent,
    LogoutReceived,
    LogoutTimedOut,
    Unknown;

    companion object {
        fun resolveState(session: Session): SessionState =
            with (session) {
                when {
                    isEnabled -> Enabled
                    isLoggedOn -> LoggedOn
                    isLogonAlreadySent -> LogonAlreadySent
                    isLogonReceived -> LogonReceived
                    isLogonSendNeeded -> LogonSentNeeded
                    isLogonSent -> LogonSent
                    isLogonTimedOut -> LogonTimeout
                    isLogoutSent -> LogoutSent
                    isLogoutReceived -> LogoutReceived
                    isLogoutTimedOut -> LogoutTimedOut
                    else -> Unknown
                }
            }
    }
}