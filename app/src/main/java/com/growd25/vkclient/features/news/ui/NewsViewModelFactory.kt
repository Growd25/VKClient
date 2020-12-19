package com.growd25.homework.features.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.growd25.homework.features.news.presentation.NewsStateMachine
import com.growd25.homework.features.news.presentation.NewsViewModel
import javax.inject.Inject

class NewsViewModelFactory @Inject constructor(private val newsStateMachine: NewsStateMachine) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(newsStateMachine) as T
        }
        error("Unknown ViewModel class")
    }
}
