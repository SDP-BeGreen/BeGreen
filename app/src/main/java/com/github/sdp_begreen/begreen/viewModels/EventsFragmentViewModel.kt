package com.github.sdp_begreen.begreen.viewModels

import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.firebase.eventServices.EventParticipantService
import com.github.sdp_begreen.begreen.firebase.eventServices.EventService
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.models.event.Event
import com.github.sdp_begreen.begreen.models.event.EventParticipant
import com.github.sdp_begreen.begreen.utils.checkRootPathMatchEventClassImpl
import com.github.sdp_begreen.begreen.utils.checkRootPathMatchParticipantClassImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

/**
 * View model to use with fragments that display a list of event
 *
 * @param currentUser The flow that emit currently connected user
 * @param rootPath The root path to where to look for the event in the database
 * @param eventClass The actual event class for which we are using this view model
 * @param participantClass The actual event participant class for which we are using this view model
 */
class EventsFragmentViewModel<T : Event<T>, K : EventParticipant>(
    private val currentUser: StateFlow<User?>,
    private val rootPath: RootPath,
    private val eventClass: Class<T>,
    private val participantClass: Class<K>
) : ViewModel() {

    /**
     * Ensure that the implementation of Event and EventParticipant are coherent with the root path
     * that has been required
     */
    init {
        checkRootPathMatchEventClassImpl(rootPath, eventClass)
        checkRootPathMatchParticipantClassImpl(rootPath, participantClass)
    }

    private val eventService by inject<EventService>(EventService::class.java)
    private val participantService by inject<EventParticipantService>(EventParticipantService::class.java)

    private val mutableParticipationMap: MutableStateFlow<Map<String, Boolean>> =
        MutableStateFlow(emptyMap())

    /**
     * Map containing information whether the current logged in user is subscribed or not
     * to a particular event
     */
    val participationMap = mutableParticipationMap.asStateFlow()

    /**
     * Flow of all the events implementation we are interested in (i.e. Meetings or Contests)
     */
    val allEvents = flow {
        eventService.getAllEvents(rootPath, eventClass).collect {
            emit(it)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        // Dynamically generate the map that contains to which event we are subscribed
        // will emit a new value if the the user changes, or if the list of event changes
        viewModelScope.launch {
            // subscribe to user connection changes
            currentUser.mapNotNull { it?.id }.combine(allEvents) { userId, events ->
                events.mapNotNull { event ->
                    event.id?.let { id ->
                        id to participantService.getAllParticipants(
                            rootPath,
                            id,
                            participantClass
                        ).first().map { it.id }
                    }
                }.associate {
                    it.first to it.second.contains(userId)
                }
            }.collect { mutableParticipationMap.emit(it) }
        }
    }

    /**
     * Function to participate to an event
     *
     * @param eventId The event to which to participate to
     *
     * @return The event id if it was possible to participate to it (i.e. a user was logged in)
     */
    fun participate(eventId: String): String? {
        currentUser.value?.also {
            viewModelScope.launch {
                participantService.addParticipant(
                    rootPath,
                    eventId,
                    participantClass.newInstance().apply {
                        id = it.id
                    }
                )
            }
            mutableParticipationMap.tryEmit(mutableParticipationMap.value + (eventId to true))
            return eventId
        }
        return null
    }

    /**
     * Function to withdraw from an event
     *
     * @param eventId The event to which to withdraw from
     *
     * @return The event id if it was possible to withdraw from it (i.e. a user was logged in)
     */
    fun withdraw(eventId: String): String? {
        currentUser.value?.also {
            viewModelScope.launch {
                participantService.removeParticipant(rootPath, eventId, it.id)
            }
            mutableParticipationMap.tryEmit(mutableParticipationMap.value + (eventId to false))
            return eventId
        }
        return null
    }

    companion object {
        /**
         * Function to create a new Factory to instantiate the [EventsFragmentViewModel]
         */
        @Suppress("UNCHECKED_CAST")
        fun <E : Event<E>, P : EventParticipant> factory(
            currentUser: StateFlow<User?>,
            rootPath: RootPath,
            eventClass: Class<E>,
            participantClass: Class<P>
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return EventsFragmentViewModel(
                        currentUser,
                        rootPath,
                        eventClass,
                        participantClass
                    ) as T
                }
            }
    }

}
