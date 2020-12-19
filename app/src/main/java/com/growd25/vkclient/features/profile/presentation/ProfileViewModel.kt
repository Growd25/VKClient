package com.growd25.vkclient.features.profile.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.growd25.vkclient.utils.connect
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class ProfileViewModel(profileStateMachine: ProfileStateMachine) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val _posts = MutableLiveData<ProfileStateMachine.State>()
    val posts: LiveData<ProfileStateMachine.State> = _posts
    private val acceptor: Relay<ProfileStateMachine.Action> = PublishRelay.create()

    init {
        acceptor.subscribe(profileStateMachine.input)
            .connect(compositeDisposable)
        profileStateMachine.state
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state -> _posts.value = state }
            .connect(compositeDisposable)
        acceptAction(ProfileStateMachine.Action.InitAction)
    }

    fun acceptAction(action: ProfileStateMachine.Action) {
        acceptor.accept(action)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
