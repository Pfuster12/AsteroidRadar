package com.udacity.asteroidradar.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R

/**
 * Asteroid Feed Adapter.
 */
class AsteroidFeedAdapter(private val context: Context?,
                          private val asteroids: List<Asteroid>,
                          private val itemClick: (pos: Int) -> Unit) : RecyclerView.Adapter<AsteroidViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.asteroid_list_item,
                parent,
                false),
            itemClick)
    }

    override fun getItemCount() = asteroids.size

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val asteroid = asteroids[position]
        holder.name.text = asteroid.codename
        holder.date.text = asteroid.closeApproachDate

        holder.icon.isSelected = asteroid.isPotentiallyHazardous

        holder.icon.contentDescription = if (asteroid.isPotentiallyHazardous) {
            context?.getString(R.string.hazard_icon_content_desc)
        } else {
            context?.getString(R.string.non_hazard_icon_content_desc)
        }
    }
}

class AsteroidViewHolder(itemView: View,
                         private val itemClick: (pos: Int) -> Unit)
    : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById(R.id.asteroid_item_name)
    val date: TextView = itemView.findViewById(R.id.asteroid_item_date)
    val icon: ImageView = itemView.findViewById(R.id.asteroid_item_hazard_icon)
    init {
        itemView.setOnClickListener { itemClick.invoke(adapterPosition) }
    }
}