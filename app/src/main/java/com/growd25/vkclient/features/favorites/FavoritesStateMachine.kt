package com.growd25.vkclient.features.favorites

import com.freeletics.rxredux.StateAccessor
import com.freeletics.rxredux.reduxStore
import com.growd25.vkclient.features.global.model.PostItem
import com.growd25.vkclient.repository.PostRepository
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import javax.inject.Inject

class FavoritesStateMachine @Inject constructor(private val postRepository: PostRepository) {

    val input: Relay<Action> = PublishRelay.create()
    val state: Observable<State> = input
        .reduxStore(
            initialState = State(),
            sideEffects = listOf(
                ::onPostItemFavoriteClickedSideEffect,
                ::getPostsFlowSideEffect,
                ::toggleFavoriteSideEffect,
            ),
            reducer = ::reducer
        )

    data class State(val favoritesPosts: List<PostItem> = emptyList())

    sealed class Action {
        data class OnPostItemFavoriteClicked(val postItem: PostItem) : Action()
        object GetPostsFlow : Action()
        data class OnPostsChanged(val posts: List<PostItem>) : Action()
        data class ToggleFavorite(val postItem: PostItem) : Action()
    }

    private fun reducer(state: State, action: Action): State = when (action) {
        is Action.OnPostsChanged -> state.copy(favoritesPosts = action.posts)
        else -> state
    }

    private fun onPostItemFavoriteClickedSideEffect(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> = actions.ofType(Action.OnPostItemFavoriteClicked::class.java)
        .flatMap { action ->
            postRepository.toggleFavorite(action.postItem)
                .toObservable()
        }

    private fun getPostsFlowSideEffect(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> = actions.ofType(Action.GetPostsFlow::class.java)
        .switchMap<Action> {
            postRepository.getFavoritesNewsPostsFlow()
                .map { Action.OnPostsChanged(it) }
                .toObservable()
        }

    private fun toggleFavoriteSideEffect(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> = actions.ofType(Action.ToggleFavorite::class.java)
        .switchMapCompletable { action ->
            postRepository.toggleFavorite(action.postItem)
        }
        .toObservable()

}
