package com.udacity.asteroidradar.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.NASARepository
import com.udacity.asteroidradar.api.WebService
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.PictureDayDatabase
import com.udacity.asteroidradar.utils.Resource
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class MainViewModel(applicationContext: Context) : ViewModel() {
    private val repository: NASARepository =
        NASARepository(
            // Webservice
            Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(WebService::class.java),
            // Roomdb DAO
            AsteroidDatabase.getInstance(applicationContext)
                .asteroidDao(),
            Room.databaseBuilder(
                applicationContext,
                PictureDayDatabase::class.java,
                "pic-day-db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .pictureDayDatabase(),
            viewModelScope
        )

    val asteroidFeed: LiveData<Resource<List<Asteroid>>> = repository.getAsteroidFeed()
    val pictureOfDay: LiveData<Resource<PictureOfDay>> = repository.getPictureOfDay()
}

class MainViewModelFactory(private val applicationContext: Context?): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = applicationContext?.let {
        MainViewModel(
            it
        )
    } as T
}