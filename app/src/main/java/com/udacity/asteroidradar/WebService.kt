package com.udacity.asteroidradar

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * NASA API Webservice.
 */
interface WebService {

    @GET("/feed")
    fun getNEOFeed(@Query("detailed") detailed: Boolean,
                   @Query("api_key") apiKey: String)
}