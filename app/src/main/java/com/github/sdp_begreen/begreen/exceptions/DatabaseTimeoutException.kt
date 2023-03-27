package com.github.sdp_begreen.begreen.exceptions

/**
 * Custom exception that represents a timeout when trying to access firebase database
 */
class DatabaseTimeoutException(message: String? = null, cause: Throwable? = null): Exception(message, cause)