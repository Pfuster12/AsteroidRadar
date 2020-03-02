package com.udacity.asteroidradar.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.udacity.asteroidradar.PictureOfDay

@Database(entities = [PictureOfDay::class], version = 4)
abstract class PictureDayDatabase : RoomDatabase()  {
    abstract fun pictureDayDatabase(): PictureDayDao
}