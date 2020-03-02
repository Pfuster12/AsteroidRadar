package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM Asteroid ORDER BY date(closeApproachDate) ASC")
    fun getAll(): LiveData<List<Asteroid>>

    @Transaction
    fun updateData(users: List<Asteroid>) {
        deleteAll()
        insertAll(users)
    }

    @Insert
    fun insertAll(asteroids: List<Asteroid>)

    @Query("DELETE FROM Asteroid")
    fun deleteAll()
}