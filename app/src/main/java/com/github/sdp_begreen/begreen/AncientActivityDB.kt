package com.github.sdp_begreen.begreen

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BoredActivity::class], version = 1)
abstract class AncientActivityDB : RoomDatabase() {
    abstract fun ActivityDao(): BoredActivityDao
}