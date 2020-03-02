package com.udacity.asteroidradar

/**
 * Interface for a UI element bound by a generic resource provided by a Repository.
 */
interface ResourceBoundUI<T> {
    fun observeViewModel()

    fun bindViewModelData(data: T)

    fun loading()

    fun idle()

    fun empty()

    fun error()
}