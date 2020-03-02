package com.udacity.asteroidradar.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.Asteroid

import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentDetailBinding

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding

    private var asteroid: Asteroid? = null

    companion object {
        fun getInstance(asteroid: Asteroid): DetailFragment {
            val fragment = DetailFragment()
            fragment.asteroid = asteroid
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentDetailBinding.inflate(inflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindAsteroidData()

        binding.detailsHelpButton.setOnClickListener {
            context?.also {
                AlertDialog.Builder(it)
                .setMessage(R.string.astronomica_unit_explanation)
                .setPositiveButton(R.string.help_close_button) { dialog, _ -> dialog.dismiss() }
                .create()
                    .show()
            }
        }
    }

    private fun bindAsteroidData() {
        asteroid?.also { asteroid ->

            binding.detailsImage.contentDescription = if (asteroid.isPotentiallyHazardous) {
                getString(R.string.potentially_hazardous_asteroid_image)
            } else {
                getString(R.string.not_hazardous_asteroid_image)
            }

            if (asteroid.isPotentiallyHazardous) {
                Picasso.with(context)
                    .load(R.drawable.asteroid_hazardous)
                    .into(binding.detailsImage)
            } else {
                Picasso.with(context)
                    .load(R.drawable.asteroid_safe)
                    .into(binding.detailsImage)
            }

            binding.detailsCloseApproachDate.text = asteroid.closeApproachDate
            binding.detailsAbsoluteMagnitude.text = getString(R.string.astronomical_unit_format, asteroid.absoluteMagnitude)
            binding.detailsEstimatedDiameter.text = getString(R.string.km_unit_format, asteroid.estimatedDiameter)
            binding.detailsRelativeVelocity.text = getString(R.string.km_s_unit_format, asteroid.relativeVelocity)
            binding.detailsDistanceEarth.text = getString(R.string.astronomical_unit_format, asteroid.distanceFromEarth)
        }
    }
}
