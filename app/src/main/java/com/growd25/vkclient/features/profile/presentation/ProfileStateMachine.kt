package com.growd25.vkclient.features.profile.presentation

import com.freeletics.rxredux.StateAccessor
import com.freeletics.rxredux.reduxStore
import com.growd25.vkclient.features.global.model.PostItem
import com.growd25.vkclient.repository.PostRepository
import com.growd25.vkclient.data.db.entity.ProfileEntity
import com.growd25.vkclient.repository.ProfileRepository
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ProfileStateMachine @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val postRepository: PostRepository
) {

    val input: Relay<Action> = PublishRelay.create()
    val state: Observable<State> = input
        .reduxStore(
            initialState = State.Loading,
            sideEffects = listOf(
                ::initSideEffect,
                ::subscribeDataBaseSideEffect,
                ::createPostSideEffect
            ),
            reducer = ::reducer
        )

    private fun initSideEffect(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> {
        return actions.ofType(Action.InitAction::class.java)
            .switchMap {
                Observable.merge(listOf(
                    profileRepository.loadProfile()
                        .flatMapObservable { profileEntity ->
                            profileRepository.insertProfile(profileEntity)
                                .toObservable<Action>()
                        }
                        .onErrorReturn { th ->
                            Action.FailedLoadProfile(th)
                        },
                    postRepository.fetchWallPosts()
                        .toObservable<Action>()
                        .onErrorReturn { th ->
                            Action.FailedLoadProfile(th)
                        }
                ))
            }
    }

    private fun subscribeDataBaseSideEffect(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> {
        return actions.ofType(Action.SubscribeDataBase::class.java)
            .switchMap {
                Observable.merge(
                    listOf(
                        profileRepository.getProfileFlow()
                            .subscribeOn(Schedulers.io())
                            .toObservable()
                            .map<Action> { profilePostEntity ->
                                Action.OnProfileReceived(profilePostEntity)
                            }
                            .onErrorReturn { th ->
                                Action.OnProfileReceivedFailed(th)
                            },
                        postRepository.getWallPostsFlow()
                            .subscribeOn(Schedulers.io())
                            .toObservable()
                            .map<Action> { profilePosts -> Action.OnPostsReceived(profilePosts) }
                            .onErrorReturn { th ->
                                Action.OnProfileReceivedFailed(th)
                            }
                    )
                )
            }
    }

    private fun createPostSideEffect(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> {
        return actions.ofType(Action.CreatePostAction::class.java)
            .switchMap { action ->
                val profileId = (state() as? State.Success)?.profileEntity?.id
                if (profileId != null) {
                    postRepository.createPost(profileId, action.messagePost)
                } else {
                    Completable.error(IllegalStateException("profileId is null"))
                }
                    .toObservable<Action>()
                    .map<Action> { Action.CreatePostCompleteAction }
                    .onErrorReturn { Action.CreatePostErrorAction }
            }
    }

    private fun reducer(state: State, action: Action): State {
        return when (action) {
            is Action.InitAction, Action.SubscribeDataBase -> state
            is Action.FailedLoadProfile -> State.Error
            is Action.OnProfileReceived -> {
                if (state is State.Success) {
                    state.copy(profileEntity = action.profile)
                } else {
                    State.Success(posts = emptyList(), profileEntity = action.profile)
                }
            }
            is Action.OnPostsReceived -> {
                if (state is State.Success) {
                    state.copy(posts = action.posts)
                } else {
                    State.Success(posts = action.posts, profileEntity = null)
                }
            }
            is Action.OnProfileReceivedFailed -> State.Error
            is Action.OnPostsReceivedFailed -> State.Error
            is Action.CreatePostAction -> state
            is Action.CreatePostErrorAction -> State.Error
            is Action.CreatePostCompleteAction -> state
        }
    }

    sealed class Action {
        object InitAction : Action()
        data class FailedLoadProfile(val e: Throwable) : Action()
        data class CreatePostAction(val messagePost: String) : Action()
        object CreatePostErrorAction : Action()
        object SubscribeDataBase : Action()
        object CreatePostCompleteAction : Action()
        data class OnProfileReceived(val profile: ProfileEntity) : Action()
        data class OnPostsReceived(val posts: List<PostItem>) : Action()
        data class OnProfileReceivedFailed(val e: Throwable) : Action()
        data class OnPostsReceivedFailed(val e: Throwable) : Action()
    }

    sealed class State {
        object Error : State()
        object Loading : State()
        data class Success(
            val posts: List<PostItem> = emptyList(),
            var profileEntity: ProfileEntity?,
            val postIsCreated: Boolean = false,
            val isRefreshing: Boolean = false
        ) : State()
    }
}
