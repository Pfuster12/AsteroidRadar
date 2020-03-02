package com.udacity.asteroidradar.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.Asteroid

import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.ResourceBoundUI
import com.udacity.asteroidradar.databinding.FragmentDetailBinding
import com.udacity.asteroidradar.main.MainViewModel
import com.udacity.asteroidradar.main.MainViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment(), ResourceBoundUI<Asteroid> {
    private lateinit var binding: FragmentDetailBinding

    // Shared ViewModel
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(activity?.applicationContext)
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
        empty()
        observeViewModel()

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

    override fun observeViewModel() {
        viewModel.selectedAsteroid.observe(viewLifecycleOwner, Observer { resource ->
            bindViewModelData(resource)

            idle()
        })
    }

    override fun bindViewModelData(data: Asteroid) {
        data.also { asteroid ->
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

    override fun loading() {

    }

    override fun idle() {
        binding.detailsEmpty.visibility = View.GONE
        binding.detailsAsteroidDetails.visibility = View.VISIBLE
    }

    override fun empty() {
        binding.detailsAsteroidDetails.visibility = View.GONE
        binding.detailsEmpty.visibility = View.VISIBLE
    }

    override fun error() {
        binding.detailsAsteroidDetails.visibility = View.GONE
        binding.detailsEmpty.visibility = View.VISIBLE
    }
}
