package com.github.sdp_begreen.begreen.firebase

/**
 * Enum that represents the root path for different element to be stored in the database
 */
enum class RootPath(val eventPath: String, val participantPath: String) {
    MEETINGS("meetings", "meetingIdsList"),
    CONTESTS("contests", "contestIdsList"),
}