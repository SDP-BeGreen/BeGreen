package com.github.sdp_begreen.begreen.models.event

data class MeetingParticipant(
    override var id: String? = null,
) : EventParticipant {

    override fun toString(): String {
        return "Participant: $id"
    }
}
