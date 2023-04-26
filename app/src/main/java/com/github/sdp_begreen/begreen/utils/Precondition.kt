package com.github.sdp_begreen.begreen.utils

import java.lang.IllegalArgumentException

/**
 * Function to call in order to check if an argument pass a precondition
 *
 * @param isTrue The condition to test
 * @param message The optional error message to add to the error in case the check fails
 * @throws IllegalArgumentException Throw if the argument did not pass the check
 */
fun checkArgument(isTrue: Boolean, message: String?) {
    if(!isTrue) throw IllegalArgumentException(message ?: "")
}