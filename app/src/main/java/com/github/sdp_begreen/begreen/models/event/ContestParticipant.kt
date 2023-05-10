package com.github.sdp_begreen.begreen.models.event

data class ContestParticipant(
    override var id: String? = null,
    var score: Int? = 0
) : EventParticipant {

    override fun toString(): String {
        return "Participant: $id, score: $score"
    }
}
