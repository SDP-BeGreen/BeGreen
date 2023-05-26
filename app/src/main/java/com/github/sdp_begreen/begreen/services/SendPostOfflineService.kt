package com.github.sdp_begreen.begreen.services

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.Lifecycle
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import kotlinx.coroutines.CoroutineScope

interface SendPostOfflineService {

    /**
     * Call this function to enable the offline support for the send post in the application
     *
     * This function is responsible to setup the mechanism to send the post online once the
     * connection has been reestablished
     */
    fun initSendPostOfflineSupport(scope: CoroutineScope, lifecycle: Lifecycle, context: Context)

    /**
     * Function to perform the posting of the picture on the database
     */
    //suspend fun postPicture(metadata: TrashPhotoMetadata?, user: User, bitmap: Bitmap, index: Int)

    /**
     * Function to save the post in memory while waiting to have connection again
     */
    fun savePost(metadata: TrashPhotoMetadata, user: User, pictureUri: String, context: Context, returnToCamera: () -> Unit)


}