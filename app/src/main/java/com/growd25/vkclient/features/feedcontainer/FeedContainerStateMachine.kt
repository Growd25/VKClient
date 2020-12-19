package com.growd25.vkclient.features.feedcontainer

import com.freeletics.rxredux.StateAccessor
import com.freeletics.rxredux.reduxStore
import com.growd25.vkclient.repository.PostRepository
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import javax.inject.Inject

class FeedContainerStateMachine @Inject constructor(private val postRepository: PostRepository) {
    val input: Relay<Action> = PublishRelay.create()
    val state: Observable<FeedContainerState> = input
        .reduxStore(
            initialState = FeedContainerState(0),
            sideEffects = listOf(
                ::favoritesPostsCountSideEffect
            ),
            reducer = ::reducer
        )

    private fun favoritesPostsCountSideEffect(
        actions: Observable<Action>,
        state: StateAccessor<FeedContainerState>
    ): Observable<Action> {
        return actions
            .ofType(Action.FavoritesPostsCountAction::class.java)
            .switchMap {
                postRepository.getFavoritesPostsCountFlow()
                    .toObservable()
            }
            .map { count ->
                Action.FavoritesPostsCountCompleteAction(count)
            }
    }

    private fun reducer(state: FeedContainerState, action: Action): FeedContainerState {
        return when (action) {
            is Action.FavoritesPostsCountAction -> state
            is Action.FavoritesPostsCountCompleteAction -> state.copy(favoritesPostsCount = action.favoritesPostsCount)
        }
    }

    sealed class Action {
        object FavoritesPostsCountAction : Action()
        data class FavoritesPostsCountCompleteAction(val favoritesPostsCount: Int) : Action()
    }

    data class FeedContainerState(
        val favoritesPostsCount: Int,
        val isLoading: Boolean = false,
        val isError: Boolean = false,
    )
}
