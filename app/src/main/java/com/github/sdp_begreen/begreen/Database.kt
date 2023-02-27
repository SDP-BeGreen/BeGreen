package com.github.sdp_begreen.begreen

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

/**
 * Abstract implementation of a simple database
 */
abstract class Database {

    // default database used for the app
    companion object {
        var db: Database = FirebaseDB()
    }

    /**
     * Return the value associated to the given key
     */
    abstract operator fun get(key: String): CompletableFuture<String>

    /**
     * Change the value of the key or create a tuple (key, value) if the given key did not exist
     */
    abstract operator fun set(key: String, value: String)
}