package com.github.sdp_begreen.begreen.utils

import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.event.Contest
import com.github.sdp_begreen.begreen.models.event.ContestParticipant
import com.github.sdp_begreen.begreen.models.event.Event
import com.github.sdp_begreen.begreen.models.event.EventParticipant
import com.github.sdp_begreen.begreen.models.event.Meeting
import com.github.sdp_begreen.begreen.models.event.MeetingParticipant
import java.lang.IllegalArgumentException

/**
 * Function to call in order to check if an argument pass a precondition
 *
 * @param isTrue The condition to test
 * @param message The optional error message to add to the error in case the check fails
 * @throws IllegalArgumentException Throw if the argument did not pass the check
 */
fun checkArgument(isTrue: Boolean, message: String = "") {
    if (!isTrue) throw IllegalArgumentException(message)
}

private val matchEventClassImplErrorMsg =
    { rootPathName: String, eventImplName: String ->
        "The root path is of type $rootPathName but the expected event type is $eventImplName"
    }

private val matchParticipantClassImplErrorMsg =
    { rootPathName: String, eventImplName: String ->
        "The root path is of type $rootPathName but the expected participant type is $eventImplName"
    }

/**
 * Function to call in order to check that the [Event] class implementation is coherent with
 * the requested root path where to search for the data
 *
 * @param rootPath The requested root path, where to look for the data
 * @param eventImpl The [Event] class implementation
 */
fun <T : Event<T>> checkRootPathMatchEventClassImpl(rootPath: RootPath, eventImpl: Class<T>) {
    val actualClass = when(rootPath) {
        RootPath.MEETINGS -> Meeting::class.java
        RootPath.CONTESTS -> Contest::class.java
    }

    checkArgument(
        eventImpl.isAssignableFrom(actualClass),
        matchEventClassImplErrorMsg(rootPath.name, eventImpl.simpleName)
    )
}

/**
 * Function to call in order to check that the [EventParticipant] class implementation is coherent with
 * the requested root path where to search for the data
 *
 * @param rootPath The requested root path, where to look for the data
 * @param participantImpl The [EventParticipant] class implementation
 */
fun <T : EventParticipant> checkRootPathMatchParticipantClassImpl(
    rootPath: RootPath,
    participantImpl: Class<T>
) {
    val actualClass = when(rootPath) {
        RootPath.MEETINGS -> MeetingParticipant::class.java
        RootPath.CONTESTS -> ContestParticipant::class.java
    }

    checkArgument(
        participantImpl.isAssignableFrom(actualClass),
        matchParticipantClassImplErrorMsg(rootPath.name, participantImpl.simpleName)
    )
}