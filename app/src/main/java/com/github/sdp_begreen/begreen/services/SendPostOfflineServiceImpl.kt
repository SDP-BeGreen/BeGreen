package com.github.sdp_begreen.begreen.services

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.github.sdp_begreen.begreen.firebase.ConnectionService
import com.github.sdp_begreen.begreen.firebase.FirebaseDB
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.utils.TinyDB
import com.github.sdp_begreen.begreen.utils.TinyDBKey
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SendPostOfflineServiceImpl() : SendPostOfflineService {

    private val connectionService by inject<ConnectionService>(ConnectionService::class.java)
    private val db by inject<FirebaseDB>(FirebaseDB::class.java)

    override fun initSendPostOfflineSupport(scope: CoroutineScope, lifecycle: Lifecycle, context: Context) {
        val tinyDB = TinyDB(context)
        scope.launch {
            connectionService.getFlowConnectionStatus()/*.flowWithLifecycle(lifecycle)*/.collect {
                if (it) {
                    Log.d("Send Post Offline Service", "connected")

                    // Handle resending of pending posts
                    val metas = tinyDB.getListObject(TinyDBKey.METAS, TrashPhotoMetadata::class.java)
                    val users = tinyDB.getListObject(TinyDBKey.USERS, User::class.java)
                    val pictureUris = tinyDB.getListString(TinyDBKey.PICTURE_URIS)

                    // If there are bitmaps, decode them and attempt to update the user
                    if (pictureUris.isNotEmpty()) {

                        pictureUris.forEachIndexed { idx, uri ->
                            val bitmap = Picasso.Builder(context).build().load(uri).get()
                            postPicture(metas[idx], users[idx], bitmap)
                        }
                    }

                    tinyDB.remove(TinyDBKey.METAS)
                    tinyDB.remove(TinyDBKey.PICTURE_URIS)
                    tinyDB.remove(TinyDBKey.USERS)
                } else {
                    Log.d("Send Post Offline Service", "not connected")
                }
            }
        }
    }

    override fun savePost(
        metadata: TrashPhotoMetadata,
        user: User,
        pictureUri: String,
        context: Context,
        returnToCamera: () -> Unit
    ) {

        val tinyDB = TinyDB(context)
        Log.d("Send Post Offline Service", "passed in save")

        // Retrieve existing metadata and add current one.
        val metas = tinyDB.getListObject(TinyDBKey.METAS, TrashPhotoMetadata::class.java)
        val newMetas = metas + metadata
        tinyDB.putListObject(TinyDBKey.METAS, newMetas)

        // Retrieve existing user data and add current user.
        val users = tinyDB.getListObject(TinyDBKey.USERS, User::class.java)
        val newUsers = users + user
        tinyDB.putListObject(TinyDBKey.USERS, newUsers)

        val imageUris = tinyDB.getListString(TinyDBKey.PICTURE_URIS)
        val newImageUris = imageUris + pictureUri
        tinyDB.putListString(TinyDBKey.PICTURE_URIS, newImageUris)

        Toast.makeText(
            context,
            "Your post will be automatically sent once online.",
            Toast.LENGTH_LONG
        ).show()

        returnToCamera()
    }

    // Function to update user in the database. Takes metadata, user and bitmap as parameters.
    private suspend fun postPicture(
        metadata: TrashPhotoMetadata?,
        user: User,
        bitmap: Bitmap,
    ) {

        // Add new photo to the database
        val storedMetadata = metadata?.let {
            db.addTrashPhoto(bitmap, metadata)
        }

        storedMetadata?.let {

            // Update user's metadata and score based on the new photo
            user.addPhotoMetadata(it)
            user.score += it.trashCategory?.value ?: 0

            // Update the user in the database
            db.addUser(user, user.id)
        }
    }
}