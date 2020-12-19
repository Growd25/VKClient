package com.growd25.vkclient.features.createpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.growd25.vkclient.repository.PostRepository
import com.growd25.vkclient.repository.ProfileRepository
import javax.inject.Inject

class CreateViewModelFactory @Inject constructor(
    private val postRepository: PostRepository,
    private val profileRepository: ProfileRepository
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreatePostViewModel::class.java)) {
            return CreatePostViewModel(postRepository, profileRepository) as T
        }
        error("Unknown ViewModel class")
    }
}
