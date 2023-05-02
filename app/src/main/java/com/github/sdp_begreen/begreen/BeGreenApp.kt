package com.github.sdp_begreen.begreen

import android.app.Application
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.FirebaseAuth
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingCommentService
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingCommentServiceImpl
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingParticipantService
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingParticipantServiceImpl
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingPhotoService
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingPhotoServiceImpl
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingService
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingServiceImpl
import com.github.sdp_begreen.begreen.models.CustomLatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.io.IOException

/**
 * To properly works with emulators, we can only have one instance of the base reference to firebase
 * so store them in an object, that will be injected in each services that requires a reference
 * to firebase
 */
object FirebaseRef {
    val database: FirebaseDatabase = Firebase.database
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
    single<MeetingService> { MeetingServiceImpl }
    single<MeetingCommentService> { MeetingCommentServiceImpl }
    single<MeetingParticipantService> { MeetingParticipantServiceImpl }
    single<MeetingPhotoService> { MeetingPhotoServiceImpl }
}

/**
 * Main entry point of BeGreen application
 */
class BeGreenApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val geocoderModule = module {
            single<GeocodingAPI> { GeocodingApiImpl(androidContext()) }
        }

        //
        startKoin {
            androidContext(this@BeGreenApp)
            modules(productionDbModule, geocoderModule)
        }

    }
}

// TODO if it works extract it in a custom interface

class GeocodingApiImpl(context: Context) : GeocodingAPI {

    private val geocoder: Geocoder

    init {
        geocoder = Geocoder(context)
    }

    override suspend fun getAddresses(latLng: CustomLatLng, maxResult: Int): MutableList<Address>? {
        latLng.latitude?.also { lat ->
            latLng.longitude?.also { lon ->
                return geocoder.getFromLocation(lat, lon, maxResult)
                //textView.text = addresses?.first()?.locality
            }
        }

        return mutableListOf()
    }
}

interface GeocodingAPI {
    @Throws(IOException::class)
    suspend fun getAddresses(latLng: CustomLatLng, maxResult: Int): MutableList<Address>?
}