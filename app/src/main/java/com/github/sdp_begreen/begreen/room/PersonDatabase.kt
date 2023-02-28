package com.github.sdp_begreen.begreen.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Person::class], exportSchema = false, version = 1)

abstract class PersonDatabase : RoomDatabase() {
    companion object {
        private const val DB_NAME: String = "person_db"
        @Volatile private var instance: PersonDatabase? = null

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context, PersonDatabase::class.java, DB_NAME).build()
        operator fun invoke(context: Context) = instance ?: {
            instance ?: buildDatabase(context).also{ instance = it}
        }


    }
    abstract fun personDao() : PersonDao
}