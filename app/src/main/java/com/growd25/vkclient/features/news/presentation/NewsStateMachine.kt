package com.growd25.homework.features.news.presentation

import com.freeletics.rxredux.StateAccessor
import com.freeletics.rxredux.reduxStore
import com.growd25.vkclient.features.global.model.PaginatorState
import com.growd25.vkclient.features.global.model.PostItem
import com.growd25.vkclient.repository.PostRepository
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import javax.inject.Inject

class NewsStateMachine @Inject constructor(
    private val postRepository: PostRepository
) {
    val input: Relay<Action> = PublishRelay.create()
    val state: Observable<State> = input
        .reduxStore(
            initialState = State.Loading,
            sideEffects = listOf(
                ::onPostItemFavoriteClickedSideEffect,
                ::getPostsFlowSideEffect,
                ::fetchPostsSideEffect,
                ::toggleFavoriteSideEffect
            ),
            reducer = ::reducer
        )

    sealed class State {
        object Error : State()
        object Loading : State()
        data class Loaded(
            val posts: List<PostItem> = emptyList(),
            val nextFrom: String? = null,
            val paginatorState: PaginatorState,
            val isRefreshing: Boolean
        ) : State()

        fun canLoadNextPage(): Boolean =
            this is Loaded && this.paginatorState == PaginatorState.LOADED && this.nextFrom != null
    }

    sealed class Action {
        object OnRefresh : Action()
        data class OnPostItemFavoriteClicked(val postItem: PostItem) : Action()
        object OnScrolledToEnd : Action()
        object GetPostsFlow : Action()
        object OnRetryFetchPostsClicked : Action()
        object OnRetryFetchNewPagePostsClicked : Action()
        data class FetchPosts(val startFrom: String?) : Action()
        data class ToggleFavorite(val postItem: PostItem) : Action()
        data class OnPostsChanged(val posts: List<PostItem>) : Action()
        data class OnFetchPostsError(val startFrom: String?, val error: Throwable) : Action()
        data class OnFetchComplete(val nextFrom: String) : Action()
    }

    private fun reducer(state: State, action: Action): State = when (action) {
        is Action.OnRetryFetchPostsClicked -> {
            if(state !is State.Loading) {
                 input.accept(Action.FetchPosts(startFrom = null))
            }
            State.Loading
        }

        is Action.OnRetryFetchNewPagePostsClicked -> {
            when {
                state.canLoadNextPage() -> {
                    input.accept(Action.FetchPosts(startFrom = (state as State.Loaded).nextFrom))
                    (state as State.Loaded).copy(paginatorState = PaginatorState.LOADING)
                }
                else -> state
            }
        }


        is Action.OnScrolledToEnd -> {
            when {
                state.canLoadNextPage() -> {
                    input.accept(Action.FetchPosts(startFrom = (state as State.Loaded).nextFrom))
                    (state as State.Loaded).copy(paginatorState = PaginatorState.LOADING)
                }
                else -> state
            }
        }
        is Action.OnPostsChanged -> {
            if (state is State.Loaded) {
                state.copy(
                    isRefreshing = false,
                    paginatorState = PaginatorState.LOADED,
                    posts = action.posts
                )
            } else {
                State.Loaded(
                    isRefreshing = false,
                    paginatorState = PaginatorState.LOADED,
                    posts = action.posts
                )
            }
        }

        is Action.OnRefresh -> {
            input.accept(Action.FetchPosts(startFrom = null))
            when (state) {
                is State.Loaded -> {
                    state.copy(
                        isRefreshing = true,
                        paginatorState = PaginatorState.LOADED
                    )
                }
                else -> state
            }
        }
        is Action.OnFetchPostsError -> {
            when (state) {
                is State.Loaded -> {
                    if (action.startFrom == null) {
                        state.copy(isRefreshing = false)
                    } else {
                        state.copy(paginatorState = PaginatorState.ERROR)
                    }
                }
                else -> State.Error
            }
        }

        is Action.OnFetchComplete -> {
            when (state) {
                is State.Loaded -> {
                    state.copy(nextFrom = action.nextFrom)
                }
                else -> {
                    State.Loaded(
                        nextFrom = action.nextFrom,
                        paginatorState = PaginatorState.LOADED,
                        isRefreshing = false
                    )
                }
            }
        }
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
        .switchMap {
            postRepository.getNewsPostsFlow()
                .map(Action::OnPostsChanged)
                .toObservable()
        }

    private fun fetchPostsSideEffect(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> = actions.ofType(Action.FetchPosts::class.java)
        .switchMap { action ->
            postRepository.fetchNewsPosts(startFrom = action.startFrom)
                .map<Action>(Action::OnFetchComplete)
                .onErrorReturn { error ->
                    Action.OnFetchPostsError(action.startFrom, error)
                }
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
