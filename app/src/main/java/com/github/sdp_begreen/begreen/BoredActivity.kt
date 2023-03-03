package com.github.sdp_begreen.begreen

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BoredActivity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "first_name")
    // Check the response format of the response! See the documentation
    val activity: String,
)