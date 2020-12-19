package com.growd25.vkclient.features.feedcontainer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.growd25.vkclient.features.feedcontainer.FeedContainerStateMachine
import com.growd25.vkclient.utils.connect
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class FeedContainerViewModel(feedContainerStateMachine: FeedContainerStateMachine) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val _isFavoritesVisible = MutableLiveData<Boolean>()
    val isFavoritesVisible: LiveData<Boolean> = _isFavoritesVisible
    private val acceptor: Relay<FeedContainerStateMachine.Action> = PublishRelay.create()

    init {
        acceptor.subscribe(feedContainerStateMachine.input)
            .connect(compositeDisposable)
        feedContainerStateMachine.state
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _isFavoritesVisible.value = it.favoritesPostsCount > 0 }
            .connect(compositeDisposable)
        acceptor.accept(FeedContainerStateMachine.Action.FavoritesPostsCountAction)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
