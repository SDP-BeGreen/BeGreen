package com.github.sdp_begreen.begreen

import android.app.Application
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.FirebaseAuth
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.firebase.MeetingService
import com.github.sdp_begreen.begreen.firebase.MeetingServiceImpl
import com.google.firebase.database.DatabaseReference
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
    val databaseReference: DatabaseReference = Firebase.database.reference
    val storageReference: StorageReference = Firebase.storage.reference
}

/**
 * The database module to use in a production environment
 */
val productionDbModule = module {
    single<DB> { FirebaseDB }
    single<Auth> { FirebaseAuth() }
    single<MeetingService> { MeetingServiceImpl }
    single { FirebaseRef }
}

/**
 * Main entry point of BeGreen application
 */
class BeGreenApp : Application() {
    override fun onCreate() {
        super.onCreate()

        //
        startKoin {
            androidContext(this@BeGreenApp)
            modules(productionDbModule)
        }

    }
}