package com.github.sdp_begreen.begreen

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BoredActivityDao {
    @Query("SELECT * FROM boredactivity WHERE id = :Id ")
    fun loadById(Id: Int): BoredActivity
    @Insert
    fun insertAll(vararg users: BoredActivity)
}