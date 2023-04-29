package com.github.sdp_begreen.begreen.models

import com.github.sdp_begreen.begreen.models.meetings.Comment
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class CommentTest {

    @Test
    fun commentToStringReturnExpectedString() {
        val newComment = Comment("1", "23", body = "this is a comment")
        assertThat(newComment.toString(), `is`("23: this is a comment"))
    }
}