package com.github.sdp_begreen.begreen.models.meetings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Class that represent a comment that can be posted on a waist collection meeting
 */
@Parcelize
data class Comment(
    var commentId: String? = null,
    var author: String? = null,
    var writtenAt: Long? = null,
    var modifiedAt: Long? = null,
    var body: String? = null
) : Parcelable {

    override fun toString(): String {
        return "$author: $body"
    }
}
