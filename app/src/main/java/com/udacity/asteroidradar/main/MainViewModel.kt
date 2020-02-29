package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.NASARepository
import com.udacity.asteroidradar.api.WebService
import com.udacity.asteroidradar.utils.Resource
import retrofit2.Retrofit

class MainViewModel : ViewModel() {
    private val repository: NASARepository =
        NASARepository(
            Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .build()
                .create(WebService::class.java),
            viewModelScope
        )

    val asteroidFeed: LiveData<Resource<List<Asteroid>>> = repository.getAsteroidFeed()
}