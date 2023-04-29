package com.github.sdp_begreen.begreen.firebase.meetingServices

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.sdp_begreen.begreen.FirebaseRef
import com.github.sdp_begreen.begreen.exceptions.MeetingServiceException
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.getBytesFromStorage
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.putBytesToStorage
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.utils.checkArgument
import kotlinx.coroutines.flow.Flow
import org.koin.java.KoinJavaComponent
import java.io.ByteArrayOutputStream

object MeetingPhotoServiceImpl : MeetingPhotoService {

    private val dbRefs by KoinJavaComponent.inject<FirebaseRef>(FirebaseRef::class.java)

    private val dbRef = dbRefs.databaseReference
    private val storageRef = dbRefs.storageReference
    private const val MEETING_PATH = "meeting"
    private const val PHOTOS_PATH = "meetingPhotos"

    override suspend fun addMeetingsPhoto(
        meetingId: String,
        photoMetadata: PhotoMetadata,
        photo: Bitmap
    ): PhotoMetadata {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        checkArgument(
            !photoMetadata.takenBy.isNullOrBlank(),
            "The user that took the photo cannot be blank or null"
        )
        val photoRef = dbRef.child(MEETING_PATH).child(meetingId).child(PHOTOS_PATH)
        return photoRef.push().key?.let {
            val newPhotoMetadata = photoMetadata.copy(pictureId = it)
            val compressedImage = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.JPEG, 100, compressedImage)
            putBytesToStorage(
                storageRef.child(MEETING_PATH).child(meetingId).child(PHOTOS_PATH).child(it),
                compressedImage.toByteArray(),
                "Error while storing the photo"
            )
            FirebaseUtils.setObjToDb(
                photoRef.child(it),
                newPhotoMetadata,
                "Error while adding the metadata for the photo"
            )
        } ?: throw MeetingServiceException("Error while generating new key for photo entry")
    }

    override suspend fun getAllPhotosMetadata(meetingId: String): Flow<List<PhotoMetadata>> {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        return FirebaseUtils.getFlowOfObjects(
            dbRef.child(MEETING_PATH).child(meetingId)
                .child(PHOTOS_PATH),
            PhotoMetadata::class.java
        )
    }

    override suspend fun getPhoto(meetingId: String, photoMetadata: PhotoMetadata): Bitmap? {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        checkArgument(
            !photoMetadata.pictureId.isNullOrBlank(),
            "The picture id cannot be blank or null"
        )

        val compressedPhoto = getBytesFromStorage(
            storageRef.child(MEETING_PATH).child(meetingId).child(
                PHOTOS_PATH
            )
                .child(photoMetadata.pictureId!!),
            "Error while getting picture bytes from storage"
        )
        return BitmapFactory.decodeByteArray(compressedPhoto, 0, compressedPhoto.size)
    }

    override suspend fun removeMeetingPhoto(meetingId: String, photoMetadata: PhotoMetadata) {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        checkArgument(
            !photoMetadata.pictureId.isNullOrBlank(),
            "The picture id cannot be blank or null"
        )

        FirebaseUtils.removeObjFromStorage(
            storageRef.child(MEETING_PATH).child(meetingId)
                .child(PHOTOS_PATH)
                .child(photoMetadata.pictureId!!),
            "Error while removing the photo from the storage"
        )
        FirebaseUtils.removeObjFromDb(
            dbRef.child(MEETING_PATH).child(meetingId)
                .child(PHOTOS_PATH)
                .child(photoMetadata.pictureId!!), "Error while removing the photo metadata"
        )
    }
}
