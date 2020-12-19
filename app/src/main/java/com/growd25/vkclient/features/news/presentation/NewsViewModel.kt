package com.growd25.homework.features.news.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.growd25.vkclient.utils.connect
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class NewsViewModel(newsStateMachine: NewsStateMachine) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val _posts = MutableLiveData<NewsStateMachine.State>()
    val posts: LiveData<NewsStateMachine.State> = _posts
    private val acceptor: Relay<NewsStateMachine.Action> = PublishRelay.create()

    init {
        acceptor.subscribe(newsStateMachine.input)
            .connect(compositeDisposable)
        newsStateMachine.state
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state -> _posts.value = state }
            .connect(compositeDisposable)
        acceptAction(NewsStateMachine.Action.FetchPosts(startFrom = null))
        acceptAction(NewsStateMachine.Action.GetPostsFlow)
    }

    fun acceptAction(action: NewsStateMachine.Action) {
        acceptor.accept(action)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
