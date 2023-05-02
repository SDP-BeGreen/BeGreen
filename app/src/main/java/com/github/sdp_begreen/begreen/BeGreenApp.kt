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
import com.github.sdp_begreen.begreen.models.CustomLatLng
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.io.IOException

/**
 * The database module to use in a production environment
 */
val productionDbModule = module {
    single<DB> { FirebaseDB }
    single<Auth> { FirebaseAuth() }
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