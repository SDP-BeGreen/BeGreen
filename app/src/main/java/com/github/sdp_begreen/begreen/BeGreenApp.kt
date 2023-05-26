package com.github.sdp_begreen.begreen

import android.app.Application
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.FirebaseAuth
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.firebase.eventServices.EventParticipantService
import com.github.sdp_begreen.begreen.firebase.eventServices.EventParticipantServiceImpl
import com.github.sdp_begreen.begreen.firebase.eventServices.EventService
import com.github.sdp_begreen.begreen.firebase.eventServices.EventServiceImpl
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingCommentService
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingCommentServiceImpl
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingPhotoService
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingPhotoServiceImpl
import com.github.sdp_begreen.begreen.services.GeocodingService
import com.github.sdp_begreen.begreen.services.GeocodingServiceImpl
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * To properly works with emulators, we can only have one instance of the base reference to firebase
 * so store them in an object, that will be injected in each services that requires a reference
 * to firebase
 */
object FirebaseRef {
    val database: FirebaseDatabase = Firebase.database
    init {
        // This call must be done before any other usage of [database]. Make sure to not move that line of code!
        database.setPersistenceEnabled(true)
        database.reference.keepSynced(true)
    }
    val databaseReference: DatabaseReference = database.reference
    val storageReference: StorageReference = Firebase.storage.reference
}

/**
 * The database module to use in a production environment
 */
val productionDbModule = module {
    single { FirebaseRef }
    single<DB> { FirebaseDB }
    single<Auth> { FirebaseAuth() }
    single<EventService> { EventServiceImpl }
    single<MeetingCommentService> { MeetingCommentServiceImpl }
    single<EventParticipantService> { EventParticipantServiceImpl }
    single<MeetingPhotoService> { MeetingPhotoServiceImpl }
}

/**
 * Main entry point of BeGreen application
 */
class BeGreenApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val geocoderModule = module {
            single<GeocodingService> { GeocodingServiceImpl(androidContext()) }
        }

        //
        startKoin {
            androidContext(this@BeGreenApp)
            modules(productionDbModule, geocoderModule)
        }

    }
}
