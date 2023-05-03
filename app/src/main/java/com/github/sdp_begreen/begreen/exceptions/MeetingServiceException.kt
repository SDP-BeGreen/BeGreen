package com.github.sdp_begreen.begreen.exceptions

/**
 * Custom exception thrown if an error occurred in the WasteCollectionMeetingService
 */
class MeetingServiceException(message: String? = null, cause: Throwable? = null): Exception(message, cause)