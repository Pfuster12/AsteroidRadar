package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.udacity.asteroidradar.PictureOfDay

@Dao
interface PictureDayDao {
    @Query("SELECT * FROM pictureofday")
    fun get(): LiveData<PictureOfDay>

    @Insert
    fun insert(asteroids: PictureOfDay)
}