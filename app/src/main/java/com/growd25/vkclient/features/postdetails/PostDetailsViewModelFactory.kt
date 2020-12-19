package com.growd25.homework.features.postdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.growd25.vkclient.features.global.model.PostItem

class PostDetailsViewModelFactory (private val detailsStateMachine: PostDetailsStateMachine,private val postItem: PostItem) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostDetailsViewModel::class.java)) {
            return PostDetailsViewModel(detailsStateMachine,postItem) as T
        }
        error("Unknown ViewModel class")
    }
}
