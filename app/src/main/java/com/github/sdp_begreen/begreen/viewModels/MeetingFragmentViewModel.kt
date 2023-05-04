package com.github.sdp_begreen.begreen.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingParticipantService
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingService
import com.github.sdp_begreen.begreen.models.User
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

class MeetingFragmentViewModel(private val currentUser: StateFlow<User?>) : ViewModel() {

    private val meetingService by inject<MeetingService>(MeetingService::class.java)
    private val participantService by inject<MeetingParticipantService>(MeetingParticipantService::class.java)

    private val mutableParticipationMap: MutableStateFlow<Map<String, Boolean>> =
        MutableStateFlow(emptyMap())

    val participationMap = mutableParticipationMap.asStateFlow()
    val allMeetings = flow {
        meetingService.getAllMeetings().collect {
            emit(it)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        // Dynamically generate the map that contains to which meeting we are subscribed
        // will emit a new value if the the user changes, or if the list of meeting changes
        viewModelScope.launch {
            // subscribe to user connection changes
            currentUser.mapNotNull { it?.id }.combine(allMeetings) { userId, meetings ->
                meetings.mapNotNull { meeting ->
                    meeting.meetingId?.let { id ->
                        id to participantService.getAllParticipants(
                            id
                        ).first()
                    }
                }.associate {
                    it.first to it.second.contains(userId)
                }
            }.collect { mutableParticipationMap.emit(it) }
        }
    }

    /**
     * Function to participate to a meeting
     *
     * @param meetingId The meeting to which to participate to
     *
     * @return The meeting id if it was possible to participate to it (i.e. a user was logged in)
     */
    fun participate(meetingId: String): String? {
        currentUser.value?.also {
            viewModelScope.launch {
                participantService.addParticipant(meetingId, it.id)
            }
            mutableParticipationMap.tryEmit(mutableParticipationMap.value + (meetingId to true))
            return meetingId
        }
        return null
    }

    /**
     * Function to withdraw to a meeting
     *
     * @param meetingId The meeting to which to withdraw from
     *
     * @return The meeting id if it was possible to withdraw from it (i.e. a user was logged in)
     */
    fun withdraw(meetingId: String): String? {
        currentUser.value?.also {
            viewModelScope.launch {
                participantService.removeParticipant(meetingId, it.id)
            }
            mutableParticipationMap.tryEmit(mutableParticipationMap.value + (meetingId to false))
            return meetingId
        }
        return null
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun factory(currentUser: StateFlow<User?>): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MeetingFragmentViewModel(currentUser) as T
                }
            }
    }

}