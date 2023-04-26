package com.github.sdp_begreen.begreen.utils

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class PreconditionTest {

    @Test
    fun checkArgumentThrowForIncorrectArgument() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            checkArgument(false, "some message")
        }

        assertThat(error.message, `is`("some message"))
    }
}