package com.github.sdp_begreen.begreen.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "person")
data class Person(
    @PrimaryKey( autoGenerate = true)
    val id : Int,
    @ColumnInfo(name = "name")
    var name : String,
    @ColumnInfo(name = "city")
    var city : String
)
