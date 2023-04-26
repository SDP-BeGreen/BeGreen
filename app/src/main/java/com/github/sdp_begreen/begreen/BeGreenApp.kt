package com.github.sdp_begreen.begreen

import android.app.Application
import com.github.sdp_begreen.begreen.firebase.Auth
import com.github.sdp_begreen.begreen.firebase.DB
import com.github.sdp_begreen.begreen.firebase.FirebaseAuth
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.firebase.MeetingService
import com.github.sdp_begreen.begreen.firebase.MeetingServiceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * The database module to use in a production environment
 */
val productionDbModule = module {
    single<DB> { FirebaseDB }
    single<Auth> { FirebaseAuth() }
    single<MeetingService> { MeetingServiceImpl() }
}

/**
 * Main entry point of BeGreen application
 */
class BeGreenApp: Application() {
    override fun onCreate() {
        super.onCreate()

        //
        startKoin {
            androidContext(this@BeGreenApp)
            modules(productionDbModule)
        }

    }
}