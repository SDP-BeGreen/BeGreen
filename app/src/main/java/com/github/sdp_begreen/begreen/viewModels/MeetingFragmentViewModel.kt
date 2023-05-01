package com.github.sdp_begreen.begreen.viewModels

import android.util.Log
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
                Log.d("Print test participation view model", "participate: ${it.id}")
                participantService.addParticipant(meetingId, it.id)
            }
            mutableParticipationMap.tryEmit(mutableParticipationMap.value + (meetingId to true))
            return meetingId
        }
        Log.d("Meeting Fragment View Model", "Error while adding participant (No connectedUser)")
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
        Log.d("Meeting Fragment View Model", "Error while removing participant (No connectedUser)")
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

//TODO keep here for not, will be used to populate db
/*
private val tempMeetings = listOf(
    Meeting(
        "m1",
        "John",
        "Downtown Cleanup",
        "Let's meet up and clean up the downtown area",
        1718445600000,
        1718467200000,
        CustomLatLng(37.7749, -122.4194),
        CustomLatLng(37.7749, -122.4194)
    ),
    Meeting(
        "m2",
        "Sarah",
        "Park Cleanup",
        "Let's meet up and clean up the local park",
        1718186400000,
        1718208000000,
        CustomLatLng(37.7739, -122.4312),
        CustomLatLng(37.7739, -122.4312)
    ),
    Meeting(
        "m3",
        "Tom",
        "Beach Cleanup",
        "Let's meet up and clean up the local beach",
        1710237600000,
        1710252000000,
        CustomLatLng(46.517355, 6.628854),
        CustomLatLng(46.517355, 6.628854)
    ),
    Meeting(
        "m4",
        "Kim",
        "River Cleanup",
        "Let's meet up and clean up the local river",
        1723464000000,
        1723485600000,
        CustomLatLng(46.519075, 6.561628),
        CustomLatLng(46.519075, 6.561628)
    ),
    Meeting(
        "m5",
        "Alex",
        "Forest Cleanup",
        "Let's meet up and clean up the local forest",
        1731139200000,
        1731150000000,
        CustomLatLng(46.806832, 7.156354),
        CustomLatLng(46.806832, 7.156354)
    ),
    Meeting(
        "m6",
        "Dana",
        "Park Cleanup",
        "Let's meet up and clean up the local park",
        1746016246000,
        1746034246000,
        CustomLatLng(40.7128, -74.0060),
        CustomLatLng(40.7128, -74.0060),
    ),
    Meeting(
        "m7",
        "Emily",
        "Highway Cleanup",
        "Let's meet up and clean up the local highway",
        1752323446000,
        1752341446000,
        CustomLatLng(34.0522, -118.2437),
        CustomLatLng(34.0522, -118.2437)
    ),
    Meeting(
        "m8",
        "Grace",
        "Trail Cleanup",
        "Let's meet up and clean up the local Trail",
        1710237600000,
        1710252000000,
        CustomLatLng(46.517355, 6.628854),
        CustomLatLng(46.517355, 6.628854)
    ),
    Meeting(
        "m9",
        "Henry",
        "Lake Cleanup",
        "Let's meet up and clean up the local Lake",
        1723464000000,
        1723485600000,
        CustomLatLng(46.519075, 6.561628),
        CustomLatLng(46.519075, 6.561628)
    ),
    Meeting(
        "m10",
        "Isabella",
        "City Cleanup",
        "Let's meet up and clean up the local City",
        1731139200000,
        1731150000000,
        CustomLatLng(46.806832, 7.156354),
        CustomLatLng(46.806832, 7.156354)
    ),
    Meeting(
        "m11",
        "Jacob",
        "Campus Cleanup",
        "Let's meet up and clean up the local college campus",
        1718445600000,
        1718467200000,
        CustomLatLng(37.7749, -122.4194),
        CustomLatLng(37.7749, -122.4194)
    ),
    Meeting(
        "m12",
        "Katherine",
        "Street Cleanup",
        "Let's meet up and clean up the local street",
        1718186400000,
        1718208000000,
        CustomLatLng(37.7739, -122.4312),
        CustomLatLng(37.7739, -122.4312)
    ),
    Meeting(
        "m13",
        "Max",
        "Community Garden Cleanup",
        "Let's meet up and clean up the local Garden",
        1710237600000,
        1710252000000,
        CustomLatLng(46.517355, 6.628854),
        CustomLatLng(46.517355, 6.628854)
    ),
    Meeting(
        "m14",
        "Sophie",
        "Nature Reserve Cleanup",
        "Let's meet up and clean up the local reserve",
        1723464000000,
        1723485600000,
        CustomLatLng(46.519075, 6.561628),
        CustomLatLng(46.519075, 6.561628)
    ),
    Meeting(
        "m15",
        "Alice",
        "Coastline Cleanup",
        "Let's meet up and clean up the local coastline",
        1731139200000,
        1731150000000,
        CustomLatLng(46.806832, 7.156354),
        CustomLatLng(46.806832, 7.156354)
    )
)
*/
