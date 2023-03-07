package com.github.sdp_begreen.begreen

import java.util.concurrent.CompletableFuture

/**
 * Abstract implementation of a simple database
 */
abstract class Database {

    /**
     * default database used for the app, using Firebase services
     */
    companion object {
        var db: Database = FirebaseDB()
    }

    /**
     * Return the value associated with the given [key]
     */
    abstract operator fun get(key: String): CompletableFuture<String>

    /**
     * Create a new tuple ([key], [value]) if [key] was not present and update the value otherwise
     */
    abstract operator fun set(key: String, value: String)
}