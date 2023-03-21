package com.github.sdp_begreen.begreen

import com.github.sdp_begreen.begreen.models.Actions
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ActionsTest {
    @Test
    fun testFollowText() {
        val action = Actions.FOLLOW
        assertThat(action.text, equalTo("Follow"))
    }
    @Test
    fun testUnFollowText() {
        val action = Actions.UNFOLLOW
        assertThat(action.text, equalTo("Unfollow"))
    }
}