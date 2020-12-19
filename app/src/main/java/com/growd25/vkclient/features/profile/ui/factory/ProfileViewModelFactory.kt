package com.growd25.vkclient.features.profile.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.growd25.vkclient.features.profile.presentation.ProfileStateMachine
import com.growd25.vkclient.features.profile.presentation.ProfileViewModel
import javax.inject.Inject

class ProfileViewModelFactory @Inject constructor(private val profileStateMachine: ProfileStateMachine) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(profileStateMachine) as T
        }
        error("Unknown ViewModel class")
    }
}
