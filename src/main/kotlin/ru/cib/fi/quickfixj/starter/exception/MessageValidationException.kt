package ru.cib.fi.quickfixj.starter.exception

class MessageValidationException(message: String, throwable: Throwable): FixException(message, throwable)