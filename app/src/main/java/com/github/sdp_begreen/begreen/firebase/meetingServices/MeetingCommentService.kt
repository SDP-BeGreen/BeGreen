package com.github.sdp_begreen.begreen.firebase.meetingServices

import com.github.sdp_begreen.begreen.exceptions.EventServiceException
import com.github.sdp_begreen.begreen.models.Comment
import kotlinx.coroutines.flow.Flow

/**
 * Service to interact with the database to work with meeting's comments
 */
interface MeetingCommentService {

    /**
     * Add a comment to the meeting corresponding to the received [meetingId]
     *
     * @param meetingId The id of the meeting for which to add a comment
     * @param comment The comment to add to the meeting
     *
     * @return The comment along with its newly generated commentId
     *
     * @throws IllegalArgumentException Throw if the meetingId is blank
     * @throws EventServiceException Throw if the new key for the comment could not be generated,
     * or if an error occurred while adding it
     */
    suspend fun addComment(meetingId: String, comment: Comment): Comment

    /**
     * Modify an existing comment in the database
     *
     * @param meetingId The id of the meeting in which the comment is
     * @param userId The id of the user that wrote the comment, only the user that wrote the
     * comment initially should be able to modify it
     * @param comment The modified comment to save in the database
     *
     * @return The comment that we modified
     *
     * @throws IllegalArgumentException Throw if one of the argument does not meet the requirement
     * @throws EventServiceException Throw  if an error occurred while adding the comment
     * to the database
     */
    suspend fun modifyComment(meetingId: String, userId: String, comment: Comment): Comment

    /**
     * Get all the comments, retrieve the comment dynamically
     *
     * The returned list of comments are ordered from most recent to oldest one based on
     * when they have last been modified.
     *
     * @param meetingId The id of the meeting from which to retrieve the list of comment
     *
     * @return a flow of all the comments for a particular meeting
     *
     * @throws IllegalArgumentException Throw if the meting id is blank
     */
    suspend fun getAllComments(meetingId: String): Flow<List<Comment>>

    /**
     * Remove a comment identified by its [comment] from a meeting identified by its [meetingId]
     *
     * @param meetingId The id of the meeting from which to remove the comment
     * @param comment The comment to be removed
     * @param userId The id of the user that wrote the comment. Only the user that wrote the comment
     * can remove it
     *
     * @throws IllegalArgumentException Throw if one of the arguments does not match the requirement
     * @throws EventServiceException Throw if an error occurred while removing the comment
     */
    suspend fun removeComment(meetingId: String, comment: Comment, userId: String)
}