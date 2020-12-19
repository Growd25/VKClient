package com.growd25.homework.features.postdetails

import com.freeletics.rxredux.StateAccessor
import com.freeletics.rxredux.reduxStore
import com.growd25.vkclient.data.net.VkApi
import com.growd25.vkclient.features.global.model.PostItem
import com.growd25.vkclient.features.postdetails.CommentaryViewItem
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PostDetailsStateMachine @Inject constructor(private val vkApi: VkApi) {

    val input: Relay<Action> = PublishRelay.create()
    val state: Observable<State> = input
        .reduxStore(
            initialState = State.Loading,
            sideEffects = listOf(::sendCommentarySideEffect, ::loadCommentarySideEffect),
            reducer = ::reducer,
        )

    private fun reducer(state: State, action: Action): State {
        return when (action) {
            is Action.LoadCommentaryAction ->state
            is Action.SendCommentaryAction -> state
            is Action.SendCommentaryFailedAction -> State.Error
            is Action.UpdateCommentsPostFailedAction -> State.Error
            is Action.UpdateCommentsPostAction -> State.Success(action.comments)
        }
    }

    private fun sendCommentarySideEffect(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> {
        return actions.ofType(Action.SendCommentaryAction::class.java)
            .switchMap { sendAction->
                vkApi.createCommentary(sendAction.postId, sendAction.commentaryMessage, sendAction.ownerId)
                    .subscribeOn(Schedulers.io())
                    .toObservable()
                    .flatMapSingle {
                        vkApi.getCommentsFromNewsFeed(sendAction.ownerId,sendAction.postId)
                            .map<Action> { commentsResponse ->
                                Action.UpdateCommentsPostAction(CommentsFactory.toCommentViewItem(commentsResponse))
                            }
                    }
                    .onErrorReturn { Action.UpdateCommentsPostFailedAction }
            }
    }

    private fun loadCommentarySideEffect(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> {
        return actions.ofType(Action.LoadCommentaryAction::class.java)
            .switchMap {
                vkApi.getCommentsFromNewsFeed(it.postItem.ownerId,it.postItem.id)
                    .subscribeOn(Schedulers.io())
                    .toObservable()
                    .map<Action> { response ->
                        Action.UpdateCommentsPostAction(CommentsFactory.toCommentViewItem(response)) }
                    .onErrorReturn { Action.UpdateCommentsPostFailedAction }
            }
    }

    sealed class Action {
        data class LoadCommentaryAction(val postItem: PostItem) : Action()
        data class SendCommentaryAction(val postId: Int, val commentaryMessage: String, val ownerId: Int) : Action()
        object SendCommentaryFailedAction : Action()
        data class UpdateCommentsPostAction(val comments: List<CommentaryViewItem>) : Action()
        object UpdateCommentsPostFailedAction : Action()
    }

    sealed class State {
        object Loading : State()
        object Error : State()
        data class Success(val comments: List<CommentaryViewItem> = emptyList()) : State()
    }
}
