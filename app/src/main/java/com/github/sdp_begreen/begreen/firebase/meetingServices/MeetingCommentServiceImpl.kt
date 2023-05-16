package com.github.sdp_begreen.begreen.firebase.meetingServices

import com.github.sdp_begreen.begreen.FirebaseRef
import com.github.sdp_begreen.begreen.exceptions.EventServiceException
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.getFlowOfObjects
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.removeObjFromDb
import com.github.sdp_begreen.begreen.firebase.FirebaseUtils.setObjToDb
import com.github.sdp_begreen.begreen.firebase.RootPath
import com.github.sdp_begreen.begreen.models.Comment
import com.github.sdp_begreen.begreen.utils.checkArgument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.java.KoinJavaComponent
import java.util.Calendar

object MeetingCommentServiceImpl : MeetingCommentService {

    private val dbRefs by KoinJavaComponent.inject<FirebaseRef>(FirebaseRef::class.java)

    private val dbRef = dbRefs.databaseReference
    private val MEETINGS_PATH = RootPath.MEETINGS.path
    private const val COMMENTS_PATH = "comments"

    override suspend fun addComment(meetingId: String, comment: Comment): Comment {
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        checkArgument(
            !comment.author.isNullOrBlank(),
            "The author of the comment cannot be blank or null"
        )
        val commentReference = dbRef.child(MEETINGS_PATH).child(meetingId).child(
            COMMENTS_PATH
        )
        return commentReference.push().key?.let {
            val currTime = Calendar.getInstance().timeInMillis
            val commentWithId =
                comment.copy(commentId = it, writtenAt = currTime, modifiedAt = currTime)
            setObjToDb(
                commentReference.child(it),
                commentWithId,
                "Error while creating the comment"
            )
        } ?: throw EventServiceException("Error while generating new key for comment entry")
    }

    override suspend fun modifyComment(
        meetingId: String,
        userId: String,
        comment: Comment
    ): Comment {
        checkArgument(userId.isNotBlank(), "The user id cannot be blank")
        checkArgument(
            comment.author == userId,
            "The user that modify the comment must be its author"
        )
        checkArgument(
            !comment.commentId.isNullOrBlank(),
            "The comment to modify cannot have an empty or blank commentId"
        )
        checkArgument(
            meetingId.isNotBlank(),
            "The meeting id for which to modify a comment cannot be blank"
        )

        return setObjToDb(
            dbRef.child(MEETINGS_PATH).child(meetingId)
                .child(COMMENTS_PATH)
                .child(comment.commentId!!),
            comment.copy(modifiedAt = Calendar.getInstance().timeInMillis),
            "Error while modifying the comment"
        )
    }

    override suspend fun getAllComments(meetingId: String): Flow<List<Comment>> {
        checkArgument(meetingId.isNotBlank(), "The meeting id should not be blank")
        return getFlowOfObjects(
            dbRef.child(MEETINGS_PATH).child(meetingId)
                .child(COMMENTS_PATH).orderByChild("modifiedAt"),
            Comment::class.java
        ).map { it.reversed() }
    }

    override suspend fun removeComment(meetingId: String, comment: Comment, userId: String) {
        checkArgument(userId.isNotBlank(), "The user id cannot be blank")
        checkArgument(meetingId.isNotBlank(), "The meeting id cannot be blank")
        checkArgument(!comment.commentId.isNullOrBlank(), "The comment id cannot be blank or null")

        removeObjFromDb(
            dbRef.child(MEETINGS_PATH).child(meetingId)
                .child(COMMENTS_PATH)
                .child(comment.commentId!!), "Error while removing the comment"
        )
    }
}