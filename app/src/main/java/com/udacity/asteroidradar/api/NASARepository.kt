package com.udacity.asteroidradar.api

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.utils.*
import kotlinx.coroutines.CoroutineScope
import org.json.JSONObject

class NASARepository(private val webService: WebService,
                     private val viewModelScope: CoroutineScope) {

    fun getAsteroidFeed(): LiveData<Resource<List<Asteroid>>> {
        return object : NetworkResource<List<Asteroid>, String>(viewModelScope) {
            override suspend fun loadFromDisk(): LiveData<List<Asteroid>?> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun shouldFetch(diskResponse: List<Asteroid>?): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }.asLiveData()
    }
}