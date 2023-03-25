package com.github.sdp_begreen.begreen.exceptions

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Assert.assertThrows

@RunWith(JUnit4::class)
class DatabaseTimeoutExceptionTest {

    @Test
    fun databaseTimeoutExceptionCorrectlyThrowException() {
        assertThrows(DatabaseTimeoutException::class.java) {
            throw DatabaseTimeoutException()
        }
    }

    @Test
    fun databaseTimeoutExceptionContainsCorrectErrorMessage() {
        val err = assertThrows(DatabaseTimeoutException::class.java) {
            throw DatabaseTimeoutException("The database could not be reached")
        }

        assertThat(err.message, `is`(equalTo("The database could not be reached")))
    }

    @Test
    fun databaseTimeoutExceptionContainsCorrectCause() {
        val err = assertThrows(DatabaseTimeoutException::class.java) {
            throw DatabaseTimeoutException(cause = java.lang.IllegalArgumentException())
        }

        assertThat(err.cause, `is`(instanceOf(java.lang.IllegalArgumentException::class.java)))
    }
}