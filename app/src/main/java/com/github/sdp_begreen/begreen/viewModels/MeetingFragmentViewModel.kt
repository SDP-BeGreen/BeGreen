package com.github.sdp_begreen.begreen.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.models.meetings.Meeting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class MeetingFragmentViewModel(currentUser: StateFlow<User?>) : ViewModel() {

    private val db by inject<DB>(DB::class.java) // TODO call the correct database

    private val mutableParticipationMap: MutableStateFlow<Map<String, Boolean>> = MutableStateFlow(emptyMap())

    val participationMap = mutableParticipationMap.asStateFlow()
    val allMeetings = flow {
        getAllMeetings().collect {
            emit(it)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // TODO will come from db
    private suspend fun getAllParticipants(meetingId: String): Flow<List<String>> = flowOf(
        emptyList()
    )


    // TODO this function will be replace by actual db call
    private suspend fun getAllMeetings(): Flow<List<Meeting>> = flowOf(tempMeetings)

    init {
        // Dynamically generate the map that contains to which meeting we are subscribed
        // will emit a new value if the the user changes, or if the list of meeting changes
        viewModelScope.launch {
            currentUser.collect { cUser ->
                cUser?.also { user ->
                    allMeetings.collect { meetings ->
                        Log.d("Print test participation view model", "Passe in init")
                        mutableParticipationMap.emit(
                            meetings.mapNotNull { meeting ->
                                meeting.meetingId?.let { id ->
                                    id to getAllParticipants(id).first()
                                }
                            }.associate {
                                it.first to it.second.contains(user.id)
                            }
                        )
                    }
                }
            }
        }
    }

    fun participate(meetingId: String) {
        Log.d("Print test participation view model", "participate: $meetingId")
        // TODO do the actual call to the database as well
        mutableParticipationMap.tryEmit(mutableParticipationMap.value + (meetingId to true))
    }

    fun withdraw(meetingId: String) {
        Log.d("Print test participation view model", "withdraw: $meetingId")
        // TODO do the actual call to the database as well
        mutableParticipationMap.tryEmit(mutableParticipationMap.value - meetingId)
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