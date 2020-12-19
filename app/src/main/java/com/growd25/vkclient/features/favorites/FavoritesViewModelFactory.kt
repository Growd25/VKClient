package com.growd25.vkclient.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class FavoritesViewModelFactory @Inject constructor(private val favoritesStateMachine: FavoritesStateMachine) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesFragmentViewModel::class.java)) {
            return FavoritesFragmentViewModel(favoritesStateMachine) as T
        }
        error("Unknown ViewModel class")
    }
}
