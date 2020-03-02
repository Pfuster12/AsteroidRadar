package com.udacity.asteroidradar

import android.R
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.udacity.asteroidradar.databinding.ActivityMainBinding
import com.udacity.asteroidradar.main.MainFragment
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    companion object {
        const val PREFS_KEY = "asteroid.PREFS_KEY"
        const val WORK_SCHEDULE_PREF = "asteroid.WORK_SCHEDULE_PREF"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val workScheduled = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
            .getBoolean(WORK_SCHEDULE_PREF, false)

        if (!workScheduled) {
            setDownloadTask()
        }

        val isTablet = resources.configuration.smallestScreenWidthDp > 600
        if (!isTablet) {
            replaceFragment(MainFragment())
        }
    }

    /**
     * Enqueue the download task requirements of internet connection and device plugged in.
     */
    private fun setDownloadTask() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val saveRequest =
            PeriodicWorkRequestBuilder<DownloadWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(this)
            .enqueue(saveRequest)

        getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(WORK_SCHEDULE_PREF, true)
            .apply()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentContainer!!.id, fragment)
            .commit()
    }
}
