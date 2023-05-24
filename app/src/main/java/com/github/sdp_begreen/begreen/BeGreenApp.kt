package com.github.sdp_begreen.begreen

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.github.sdp_begreen.begreen.firebase.*
import com.github.sdp_begreen.begreen.firebase.eventServices.EventParticipantService
import com.github.sdp_begreen.begreen.firebase.eventServices.EventParticipantServiceImpl
import com.github.sdp_begreen.begreen.firebase.eventServices.EventService
import com.github.sdp_begreen.begreen.firebase.eventServices.EventServiceImpl
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingCommentService
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingCommentServiceImpl
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingPhotoService
import com.github.sdp_begreen.begreen.firebase.meetingServices.MeetingPhotoServiceImpl
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.services.GeocodingService
import com.github.sdp_begreen.begreen.services.GeocodingServiceImpl
import com.github.sdp_begreen.begreen.utils.TinyDB
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
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
    companion object {
        var applicationScope = MainScope()
    }

    private val db by inject<DB>()
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

        // Enable Firebase persistence for offline capabilities
        Firebase.database.setPersistenceEnabled(true)
        FirebaseRef.databaseReference.keepSynced(true)

        // Check for connectivity and perform necessary actions
        val connectedRef = Firebase.database.getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    Log.d("TAG", "connected")

                    // Handle resending of pending posts
                    val tinyDB = TinyDB(this@BeGreenApp)
                    val metas = tinyDB.getListObject("metas", TrashPhotoMetadata::class.java)
                    val users = tinyDB.getListObject("users", User::class.java)
                    val bitmaps = tinyDB.getListString("bitmaps")

                    // If there are bitmaps, decode them and attempt to update the user
                    if (bitmaps.isNotEmpty()) {
                        val b: ByteArray = Base64.decode(bitmaps[0], Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)

                        // Launch a coroutine to update user with new metadata
                        applicationScope.launch { updateUser(metas[0] as TrashPhotoMetadata?
                            , users[0] as User, bitmap
                        ) }

                        Toast.makeText(this@BeGreenApp, "Resending Pending Posts No."+metas.size, Toast.LENGTH_LONG).show()
                    }

                } else {
                    Log.d("TAG", "not connected")

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Listener was cancelled")
            }
        })
    }

    // Function to update user in the database. Takes metadata, user and bitmap as parameters.
    private suspend fun updateUser(metadata: TrashPhotoMetadata?, user: User, bitmap: Bitmap) {

        // Add new photo to the database
        val storedMetadata = metadata?.let {
            db.addTrashPhoto(bitmap, metadata)
        }

        storedMetadata?.let {

            // Update user's metadata and score based on the new photo
            user.addPhotoMetadata(metadata)
            user.score += metadata.trashCategory?.value ?: 0

            // Update the user in the database
            db.addUser(user, user.id)

            // Show success message
            Toast.makeText(this@BeGreenApp, R.string.photo_shared_success, Toast.LENGTH_SHORT)
                .show()

            // Clear stored metadata, bitmaps, and users from TinyDB
            val tinyDB = TinyDB(this@BeGreenApp)
            tinyDB.remove("metas")
            tinyDB.remove("bitmaps")
            tinyDB.remove("users")
        }
    }
}
