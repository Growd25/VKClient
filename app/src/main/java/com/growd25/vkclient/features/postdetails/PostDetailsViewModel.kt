package com.growd25.homework.features.postdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.growd25.vkclient.features.global.model.PostItem
import com.growd25.vkclient.utils.connect
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class PostDetailsViewModel(detailsStateMachine: PostDetailsStateMachine, postItem: PostItem) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val _posts = MutableLiveData<PostDetailsStateMachine.State>()
    val posts: LiveData<PostDetailsStateMachine.State> = _posts
    private val acceptor: Relay<PostDetailsStateMachine.Action> = PublishRelay.create()

    init {
        acceptor.subscribe(detailsStateMachine.input)
            .connect(compositeDisposable)
        detailsStateMachine.state
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state -> _posts.value = state }
            .connect(compositeDisposable)
        acceptAction(PostDetailsStateMachine.Action.LoadCommentaryAction(postItem))
    }

    fun acceptAction(action: PostDetailsStateMachine.Action) {
        acceptor.accept(action)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
