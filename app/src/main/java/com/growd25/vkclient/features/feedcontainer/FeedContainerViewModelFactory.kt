package com.growd25.vkclient.features.feedcontainer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class FeedContainerViewModelFactory @Inject constructor(private val feedContainerStateMachine: FeedContainerStateMachine) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedContainerViewModel::class.java)) {
            return FeedContainerViewModel(feedContainerStateMachine) as T
        }
        error("Unknown ViewModel class")
    }
}
