package com.github.sdp_begreen.begreen.firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.github.sdp_begreen.begreen.FirebaseRef
import com.github.sdp_begreen.begreen.exceptions.DatabaseTimeoutException
import com.github.sdp_begreen.begreen.firebase.models.FirebaseUser
import com.github.sdp_begreen.begreen.map.Bin
import com.github.sdp_begreen.begreen.models.PhotoMetadata
import com.github.sdp_begreen.begreen.models.ProfilePhotoMetadata
import com.github.sdp_begreen.begreen.models.TrashPhotoMetadata
import com.github.sdp_begreen.begreen.models.User
import com.github.sdp_begreen.begreen.utils.checkArgument
import com.google.firebase.database.*
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.koin.java.KoinJavaComponent.inject
import java.io.ByteArrayOutputStream


/**
 * Database implementation using Firebase's services
 */
object FirebaseDB: DB {

    private val dbRefs by inject<FirebaseRef>(FirebaseRef::class.java)

    private const val TAG: String = "Firebase Database"

    private const val ONE_MEGABYTE: Long = 1024 * 1024 // Maximal image size allowed (in bytes), to prevent out of memory errors
    // Realtime database ref
    private val databaseReference: DatabaseReference = dbRefs.databaseReference
    // Storage ref (for images)
    private val storageReference: StorageReference = dbRefs.storageReference
    private val connectedReference = dbRefs.database.getReference(".info/connected")
    private const val USERS_PATH = "users"
    private const val USER_PROFILE_PICTURE_METADATA = "profilePictureMetadata"
    private const val USER_POSTS = "posts"
    private const val USER_ID_ATTRIBUTE = "id"
    private const val BIN_LOCATION_PATH = "bin"
    private const val ADVICES_LOCATION_PATH = "advices"
    private const val FOLLOWERS_PATH = "followers"
    private const val FOLLOWING_PATH = "following"
    private const val FEEDBACK_PATH = "contact_us"
    private const val USER_PROFILE_PICTURE_ID_SUFFIX = "_profile_picture"

    // Logs (in the console) the connections and disconnections with the Firebase database
    // We might want to provide a new constructor that takes code to execute on connections/disconnections
    // (to show the user when he gets connected/disconnected by example)
    init {
        connectedReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue(Boolean::class.java) == true) {
                    Log.d(TAG, "connected")
                } else {
                    Log.d(TAG, "disconnected")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Connection listener was cancelled")
            }
        })
    }

    override suspend fun get(key: String): String? {

        return getNode(key).value?.let {
            it as? String
        }
    }

    override suspend fun set(key: String, value: String) {
        if (key.isBlank())
            throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

        databaseReference.child(key).setValue(value).await()
    }

    override suspend fun addUser(user: User, userId: String) {
        if (userId.isBlank())
            throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

        databaseReference.child(USERS_PATH).child(userId).setValue(FirebaseUser(user)).await()
    }

    override suspend fun getUser(userId: String): User? {

        if (userId.isBlank())
            throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

        return getNode("$USERS_PATH/$userId").getValue(FirebaseUser::class.java)?.toUser()
    }

    override suspend fun getAllUsers(): List<User> {

        return getNode(USERS_PATH).children.mapNotNull {
            it.getValue(FirebaseUser::class.java)?.toUser()
        }
    }

    override suspend fun storeUserProfilePicture(image: Bitmap, userId: String, metadata: ProfilePhotoMetadata): ProfilePhotoMetadata? {
        if (userId.isBlank())
            throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

        metadata.pictureId = "${userId}${USER_PROFILE_PICTURE_ID_SUFFIX}"

        return storePicture(image, USER_PROFILE_PICTURE_METADATA, metadata,
            databaseReference.child(USERS_PATH).child(userId),
            storageReference.child(USERS_PATH).child(userId).child(
                USER_PROFILE_PICTURE_METADATA))
    }

    override suspend fun addTrashPhoto(image : Bitmap, trashPhotoMetadata: TrashPhotoMetadata): TrashPhotoMetadata? {

        checkArgument(
            !trashPhotoMetadata.takenBy.isNullOrBlank(),
            "The user that took the photo cannot be blank or null"
        )

        checkArgument(
            userExists(trashPhotoMetadata.takenBy!!),
            "The user doesn't exist in the database"
        )

        val newPhotoMetadata = trashPhotoMetadata.copy(pictureId = null)


        return storePicture(image, USER_POSTS, newPhotoMetadata,
            databaseReference.child(USERS_PATH).child(newPhotoMetadata.takenBy!!).child(USER_POSTS),
            storageReference.child(USERS_PATH).child(newPhotoMetadata.takenBy).child(USER_POSTS))
    }

    override suspend fun userExists(userId: String): Boolean {

        if (userId.isBlank())
            throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

        return getNode("$USERS_PATH/$userId/$USER_ID_ATTRIBUTE").exists()
    }

    override suspend fun getImage(metadata: PhotoMetadata): Bitmap? {

        val userId = metadata.takenBy ?: return null

        return metadata.pictureId?.let {
            getPicture(storageReference.child(USERS_PATH).child(userId).child(
                USER_POSTS).child(it))
        }
    }

    override suspend fun getUserProfilePicture(metadata: ProfilePhotoMetadata, userId: String): Bitmap? {

        if (userId.isBlank())
            throw java.lang.IllegalArgumentException("The userId cannot be a blank string")

        return metadata.pictureId?.let {
            getPicture(
                storageReference.child(USERS_PATH).child(userId).child(
                    USER_PROFILE_PICTURE_METADATA
                ).child(it)
            )
        }
    }

    override suspend fun addBin(bin: Bin): Boolean {

        if (bin.id != null)
            throw java.lang.IllegalArgumentException("Bin should not have an ID before being stored")

        val freshId = databaseReference.child(BIN_LOCATION_PATH).push().key ?: return false
        bin.id = freshId

        databaseReference.child(BIN_LOCATION_PATH).child(freshId).setValue(bin).await()
        return true
    }

    override suspend fun removeBin(binId: String) {
        databaseReference.child(BIN_LOCATION_PATH).child(binId).removeValue().await()
    }

    override suspend fun getAllBins(): List<Bin> {

        return getNode(BIN_LOCATION_PATH).children.mapNotNull {
            it.getValue(Bin::class.java)
        }
    }

    override suspend fun getAdvices(): Set<String> {

        return getNode(ADVICES_LOCATION_PATH).children.mapNotNull {
            it.value as? String
        }.toSet()
    }

    override suspend fun follow(followerId: String, followedId: String) {
        if (!userExists(followerId) || !userExists(followedId)) return

        // Add the "followed" user to the list of followed users of the follower
        databaseReference.child(USERS_PATH).child(followerId).child(FOLLOWING_PATH).child(followedId).setValue(true).await()
        // Add the "follower" user to the list of following users of the user being followed
        databaseReference.child(USERS_PATH).child(followedId).child(FOLLOWERS_PATH).child(followerId).setValue(true).await()
    }

    override suspend fun unfollow(followerId: String, followedId: String) {
        if (!userExists(followerId) || !userExists(followedId)) return

        // Add the "followed" user to the list of followed users of the follower
        databaseReference.child(USERS_PATH).child(followerId).child(FOLLOWING_PATH).child(followedId).removeValue().await()
        // Add the "follower" user to the list of following users of the user being followed
        databaseReference.child(USERS_PATH).child(followedId).child(FOLLOWERS_PATH).child(followerId).removeValue().await()
    }

    override suspend fun getFollowedIds(userId: String): List<String> {
        if (!userExists(userId)) return listOf()

        return getNode("$USERS_PATH/$userId/$FOLLOWING_PATH").children.mapNotNull {
            it.key
        }
    }

    override suspend fun getFollowerIds(userId: String): List<String> {
        if (!userExists(userId)) return listOf()

        return getNode("$USERS_PATH/$userId/$FOLLOWERS_PATH").children.mapNotNull {
            it.key
        }
    }

    override suspend fun getFollowers(userId: String): List<User> {
        val users = getAllUsers()
        val followerIds = getFollowerIds(userId)
        return users.filter { followerIds.contains(it.id) }
    }

    /**
     * Helper function to perform the actual call to the database to retrieve the image
     *
     * @param storageNode the node from which to retrieve the image
     * @return the retrieved [Bitmap] or null if an error occured
     */
    private suspend fun getPicture(storageNode: StorageReference): Bitmap? {
        return try {
            val compressedImage = storageNode.getBytes(ONE_MEGABYTE).await()
            BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.size)
        } catch (storageEx: StorageException) {
            Log.d(TAG, "Failed with error message: ${storageEx.message}")
            throw storageEx
        }
    }

    /**
     * Helper function to store the given [image] under the [storageNode]
     *
     * @param image The image to store
     * @param parentNode An optional string that can be used to specify the name of the key under
     * which the metadata will be stored in the db, if not specified the name of the picture will
     * be used by default
     * @param photoMetadata The metadata associated with the given image
     * @param dbNode The node in which to store the metadata
     * @param storageNode The node in which to store the image
     *
     * @return The image metadata updated with the actual picture Id under witch the image has been stored
     */
    private suspend fun <T : PhotoMetadata> storePicture(image: Bitmap, parentNode: String?, photoMetadata: T,
                                     dbNode: DatabaseReference, storageNode: StorageReference): T? {

        // if the url is not yet contained in the metadata, then generate a new uid by calling push
        val pictureId = photoMetadata.pictureId ?: dbNode.push().key ?: return null

        photoMetadata.pictureId = pictureId

        val compressedImage = compressImage(image, ONE_MEGABYTE)

        return try {
            storageNode.child(pictureId).putBytes(compressedImage.toByteArray()).await()
            dbNode.child(parentNode ?: pictureId).setValue(photoMetadata)
            return photoMetadata
        } catch (e: Error) {
            null
        }
    }

    /**
     * Helper method to compress image so its less than [maxSizeBytes]
     */
    private fun compressImage(image: Bitmap, maxSizeBytes: Long): ByteArrayOutputStream {

        // Iteratively compress the 'image' until its less than 'maxSizeBytes'

        val outputStream = ByteArrayOutputStream()
        var quality = 100
        val step = 5
        image.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        // Compress the image in a loop until it fits within the desired file size
        while (outputStream.toByteArray().size >= maxSizeBytes && quality > 0) {
            outputStream.reset()
            quality -= step
            image.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        }

        return outputStream
    }

    // Returns the node in the database at the given [path]
    private suspend fun getNode(path: String): DataSnapshot{
        return try {
            databaseReference.child(path).get().await()
        } catch (databaseEx: DatabaseException) {
            Log.d(TAG, "Failed with error message: ${databaseEx.message}")
            throw databaseEx
        }
    }

    override suspend fun addFeedback(feedback: String, userId: String, date: String) {
        try {
            databaseReference.child(FEEDBACK_PATH).child(userId).child(date).setValue(feedback)
                    .await()
        } catch (databaseEx: DatabaseException) {
            Log.d(TAG, "Failed with error message: ${databaseEx.message}")
            throw databaseEx
        }
    }

}