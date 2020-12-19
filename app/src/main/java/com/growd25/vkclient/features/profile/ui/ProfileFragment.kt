package com.growd25.vkclient.features.profile.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.growd25.vkclient.R
import com.growd25.vkclient.features.profile.presentation.ProfileStateMachine
import com.growd25.vkclient.features.profile.presentation.ProfileViewModel
import com.growd25.vkclient.features.profile.ui.adapter.ProfileAdapter
import com.growd25.vkclient.features.profile.ui.factory.ProfileViewItemFactory
import com.growd25.vkclient.features.profile.ui.factory.ProfileViewModelFactory
import com.growd25.vkclient.utils.visibility
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    @Inject
    lateinit var profileViewModelFactory: ProfileViewModelFactory

    private val profileViewModel: ProfileViewModel by viewModels { profileViewModelFactory }

    private val profileAdapter: ProfileAdapter = ProfileAdapter(
        onPostItemClicked = {},
        onCreatePostButtonClicked = { createPostListener?.onCreatePostButtonClick() },
        onFavoritesButtonClicked = {}
    )

    private var createPostListener: CreatePostListener? = null

    override fun onAttach(context: Context) {
        createPostListener = (context as CreatePostListener)
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onDetach() {
        createPostListener = null
        super.onDetach()
    }

    interface CreatePostListener {
        fun onCreatePostButtonClick()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel.posts.observe(viewLifecycleOwner, this::consumeState)
        profileRecyclerView.adapter = profileAdapter
        profileViewModel.acceptAction(ProfileStateMachine.Action.SubscribeDataBase)
        profileSwipeRefreshLayout.setOnRefreshListener {
            profileViewModel.acceptAction(
                ProfileStateMachine.Action.InitAction
            )
        }
    }

    private fun consumeState(state: ProfileStateMachine.State) {
        when (state) {
            is ProfileStateMachine.State.Error -> {
                profileSwipeRefreshLayout.visibility(false)
                errorGroup.visibility(true)
                profileProgress.visibility(false)

            }
            is ProfileStateMachine.State.Loading -> {
                profileSwipeRefreshLayout.visibility(false)
                errorGroup.visibility(false)
                profileProgress.visibility(true)

            }
            is ProfileStateMachine.State.Success -> {
                profileSwipeRefreshLayout.visibility(true)
                errorGroup.visibility(false)
                profileProgress.visibility(false)
                profileSwipeRefreshLayout.isRefreshing = state.isRefreshing

                profileAdapter.items = mutableListOf<Any>().apply {
                    addAll(ProfileViewItemFactory.buildProfile(state.profileEntity))
                    addAll(state.posts)
                }
            }
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
