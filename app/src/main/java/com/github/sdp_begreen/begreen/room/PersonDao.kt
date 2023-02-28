package com.github.sdp_begreen.begreen.room

import androidx.room.*

@Dao
interface PersonDao {
    @Query("Select * from person")
    fun getPersonList() : List<Person>
    @Query("Select * From person Where name Like :name")
    fun getPersonOfName(name: String) : Person // or LiveData<List<Person>> for observable
    @Insert
    fun insertPerson(person : Person)
    @Update
    fun updatePerson(person : Person)
    @Delete
    fun deletePerson(person : Person)
}