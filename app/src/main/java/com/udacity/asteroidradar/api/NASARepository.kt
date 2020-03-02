package com.udacity.asteroidradar.api

import androidx.lifecycle.LiveData
import com.squareup.moshi.Moshi
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.PictureDayDao
import com.udacity.asteroidradar.utils.*
import kotlinx.coroutines.CoroutineScope
import org.json.JSONObject

/**
 * Single source of truth for network and cached NASA API data.
 */
class NASARepository(private val webService: WebService,
                     private val asteroidDao: AsteroidDao,
                     private val pictureDayDao: PictureDayDao,
                     private val viewModelScope: CoroutineScope) {

    /**
     * Get the daily asteroid feed.
     */
    fun getAsteroidFeed(): LiveData<Resource<List<Asteroid>>> {
        return object : NetworkResource<List<Asteroid>, String>(viewModelScope) {
            override suspend fun loadFromDisk(): LiveData<List<Asteroid>> {
                return asteroidDao.getAll()
            }

            override fun shouldFetch(diskResponse: List<Asteroid>?): Boolean {
                return diskResponse.isNullOrEmpty()
            }

            override suspend fun fetchData(): Response<String> {
                val call = webService.getNEOFeed(apiKey = BuildConfig.NASA_API_KEY)
                val response = call.safeExecute()

                if (!response.isSuccessful || response.body().isNullOrEmpty()) {
                    return Failure(400, "Invalid Response")
                }

                return Success(response.body() as String)
            }

            override fun processResponse(response: String): List<Asteroid> {
                val json = JSONObject(response)

                return parseAsteroidsJsonResult(json)
            }

            override suspend fun saveToDisk(data: List<Asteroid>) {
                return asteroidDao.updateData(data)
            }
        }.asLiveData()
    }

    /**
     * Get Pic of the Day from NASA's API.
     */
    fun getPictureOfDay(): LiveData<Resource<PictureOfDay>> {
        return object : NetworkResource<PictureOfDay, String>(viewModelScope) {
            override suspend fun loadFromDisk(): LiveData<PictureOfDay> {
                return pictureDayDao.get()
            }

            override fun shouldFetch(diskResponse: PictureOfDay?): Boolean {
                return true
            }

            override suspend fun fetchData(): Response<String> {
                val call = webService.getPictureOfDay(apiKey = BuildConfig.NASA_API_KEY)
                val response = call.safeExecute()

                if (!response.isSuccessful || response.body().isNullOrEmpty()) {
                    return Failure(400, "Invalid Response")
                }

                return Success(response.body() as String)
            }

            override fun processResponse(response: String): PictureOfDay {
                return Moshi.Builder()
                    .build()
                    .adapter(PictureOfDay::class.java)
                    .fromJson(response)
                    ?:
                    // Return an empty picture
                    PictureOfDay(-1, "image", "", "")
            }

            override suspend fun saveToDisk(data: PictureOfDay) {
                return pictureDayDao.insert(data)
            }
        }.asLiveData()
    }
}