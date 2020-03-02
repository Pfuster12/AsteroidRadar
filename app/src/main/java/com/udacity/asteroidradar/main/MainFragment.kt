package com.udacity.asteroidradar.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.ResourceBoundUI
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.detail.DetailFragment
import com.udacity.asteroidradar.utils.Status

/**
 * Main [Fragment].
 */
class MainFragment : Fragment(), ResourceBoundUI<List<Asteroid>> {
    companion object {
        const val TAG = "Main"
    }
    private lateinit var binding: FragmentMainBinding

    private val asteroidFeed: MutableList<Asteroid> = mutableListOf()

    // Shared ViewModel
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(activity?.applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        observePictureOfDay()

        binding.mainAsteroidFeed.adapter = AsteroidFeedAdapter(context,
            asteroidFeed) { pos ->
            val fragment = DetailFragment.getInstance(asteroidFeed[pos])
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.setCustomAnimations(R.anim.fragment_open_enter, R.anim.fragment_open_exit)
                ?.replace(R.id.main_fragment_container, fragment)
                ?.addToBackStack(TAG)
                ?.commit()
        }
    }

    override fun observeViewModel() {
        viewModel.asteroidFeed.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    if (resource.data == null) {
                        empty()
                        return@Observer
                    }
                    idle()

                    bindViewModelData(resource.data)
                }
                Status.LOADING -> loading()
                Status.ERROR -> error()
            }
        })
    }

    override fun bindViewModelData(data: List<Asteroid>) {
        asteroidFeed.clear()
        asteroidFeed.addAll(data)
        binding.mainAsteroidFeed.adapter?.notifyDataSetChanged()
    }

    private fun observePictureOfDay() {
        viewModel.pictureOfDay.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    if (resource.data == null) {
                        empty()
                        return@Observer
                    }
                    idle()

                    bindPictureOfDay(resource.data)
                }
                Status.LOADING -> {}
                Status.ERROR -> {
                }
            }
        })
    }

    private fun bindPictureOfDay(data: PictureOfDay) {
        binding.mainImageDay.contentDescription = getString(
            R.string.nasa_picture_of_day_content_description_format,
            data.title)

        Picasso.with(context)
            .load(data.url)
            .into(binding.mainImageDay)
    }

    override fun loading() {
        binding.mainEmpty.visibility = View.GONE
        binding.mainError.visibility = View.GONE
        binding.mainAsteroidFeed.visibility = View.GONE

        binding.mainLoader.visibility = View.VISIBLE
    }

    override fun idle() {
        binding.mainEmpty.visibility = View.GONE
        binding.mainError.visibility = View.GONE
        binding.mainLoader.visibility = View.GONE

        binding.mainAsteroidFeed.visibility = View.VISIBLE
    }

    override fun empty() {
        binding.mainLoader.visibility = View.GONE
        binding.mainError.visibility = View.GONE
        binding.mainAsteroidFeed.visibility = View.GONE

        binding.mainEmpty.visibility = View.VISIBLE
    }

    override fun error() {
        binding.mainEmpty.visibility = View.GONE
        binding.mainLoader.visibility = View.GONE
        binding.mainAsteroidFeed.visibility = View.GONE

        binding.mainError.visibility = View.VISIBLE
    }
}
