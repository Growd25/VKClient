package com.growd25.vkclient.features.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.growd25.vkclient.utils.connect
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class FavoritesFragmentViewModel(favoritesStateMachine: FavoritesStateMachine) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val _posts = MutableLiveData<FavoritesStateMachine.State>()
    val posts: LiveData<FavoritesStateMachine.State> = _posts
    private val acceptor: Relay<FavoritesStateMachine.Action> = PublishRelay.create()

    init {
        acceptor.subscribe(favoritesStateMachine.input)
            .connect(compositeDisposable)
        favoritesStateMachine.state
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state -> _posts.value = state }
            .connect(compositeDisposable)
        acceptor.accept(FavoritesStateMachine.Action.GetPostsFlow)
    }

    fun acceptAction(action: FavoritesStateMachine.Action) {
        acceptor.accept(action)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
