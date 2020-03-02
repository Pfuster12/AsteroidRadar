package com.udacity.asteroidradar

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.squareup.moshi.Moshi
import com.udacity.asteroidradar.api.WebService
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.api.safeExecute
import com.udacity.asteroidradar.database.AsteroidDatabase
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class DownloadWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val webService = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(WebService::class.java)

        val asteroidDao = AsteroidDatabase
            .getInstance(applicationContext)
            .asteroidDao()

        val call = webService.getNEOFeed(apiKey = BuildConfig.NASA_API_KEY)
        val response = call.safeExecute()

        if (!response.isSuccessful || response.body().isNullOrEmpty()) {
            return Result.failure()
        }

         val asteroids = parseAsteroidsJsonResult(
             JSONObject(response.body() ?: "")
         )

        try {
            asteroidDao.updateData(asteroids)
        } catch (e: SQLiteConstraintException) {
            return Result.failure()
        }

        return Result.success()
    }
}