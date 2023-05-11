package com.github.sdp_begreen.begreen.firebase

/**
 * Enum that represents the root path for different element to be stored in the database
 */
enum class RootPath(val path: String) {
    MEETINGS("meetings"),
    CONTESTS("contests")
}