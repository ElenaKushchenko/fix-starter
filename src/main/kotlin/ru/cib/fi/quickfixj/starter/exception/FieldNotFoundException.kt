package ru.cib.fi.quickfixj.starter.exception

class FieldNotFoundException(message: String, throwable: Throwable): FixException(message, throwable)