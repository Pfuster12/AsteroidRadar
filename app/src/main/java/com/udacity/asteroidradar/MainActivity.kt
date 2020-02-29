package com.udacity.asteroidradar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.udacity.asteroidradar.api.WebService
import com.udacity.asteroidradar.databinding.ActivityMainBinding
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .build()
            .create(WebService::class.java)
    }
}
