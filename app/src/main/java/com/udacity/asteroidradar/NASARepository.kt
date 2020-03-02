package com.udacity.asteroidradar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import com.udacity.asteroidradar.api.WebService
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.api.safeExecute
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.PictureDayDao
import com.udacity.asteroidradar.utils.*
import kotlinx.coroutines.CoroutineScope
import org.json.JSONObject
import java.util.concurrent.TimeUnit

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
                return MutableLiveData(asteroidDao.getAll())
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

            override suspend fun saveToDisk(data: List<Asteroid>): Boolean {
                val ids = asteroidDao.updateData(data)
                return ids.isNotEmpty()
            }
        }.asLiveData()
    }

    /**
     * Get Pic of the Day from NASA's API.
     */
    fun getPictureOfDay(): LiveData<Resource<PictureOfDay>> {
        return object : NetworkResource<PictureOfDay, String>(viewModelScope) {
            override suspend fun loadFromDisk(): LiveData<PictureOfDay> {
                return MutableLiveData(pictureDayDao.get())
            }

            override fun shouldFetch(diskResponse: PictureOfDay?): Boolean {
                // Fetch if 24hr timestamp has expired
                return diskResponse == null
                        || diskResponse.timestamp +
                        TimeUnit.MILLISECONDS
                            .convert(24L, TimeUnit.HOURS) < System.currentTimeMillis()
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
                val pic = Moshi.Builder()
                    .build()
                    .adapter(PictureOfDay::class.java)
                    .fromJson(response)
                    ?:
                    // Return an empty picture
                    PictureOfDay(-1, "image", "", "")
                pic.timestamp = System.currentTimeMillis()
                return pic
            }

            override suspend fun saveToDisk(data: PictureOfDay): Boolean {
                return pictureDayDao.updateData(data) > 0
            }
        }.asLiveData()
    }
}