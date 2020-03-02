package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.udacity.asteroidradar.Asteroid

@Database(entities = [Asteroid::class], version = 2)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract fun asteroidDao(): AsteroidDao

    companion object {
        private var instance: AsteroidDatabase? = null

        fun getInstance(context: Context): AsteroidDatabase {
            if (instance == null) {
                instance = Room
                    .databaseBuilder(
                        context.applicationContext,
                        AsteroidDatabase::class.java,
                        "asteroid-db"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }
}