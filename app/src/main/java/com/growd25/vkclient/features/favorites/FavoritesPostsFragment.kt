package com.growd25.vkclient.features.favorites

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.growd25.vkclient.features.global.adapter.PostAdapter
import com.growd25.vkclient.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_favorites_posts.*
import javax.inject.Inject

class FavoritesPostsFragment : Fragment(R.layout.fragment_favorites_posts) {

    private lateinit var favoritesFragmentViewModel: FavoritesFragmentViewModel

    @Inject
    lateinit var favoritesViewModelFactory: FavoritesViewModelFactory

    private val adapter = PostAdapter(
        onPostItemClicked = {},
        onFavoritesButtonClicked = { postItem ->
            favoritesFragmentViewModel.acceptAction(FavoritesStateMachine.Action.OnPostItemFavoriteClicked(postItem))
        },
        onRetryClicked = {}
    )

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        favoritesRecyclerView.adapter = adapter
        favoritesRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        favoritesFragmentViewModel = ViewModelProvider(this, favoritesViewModelFactory)
            .get(FavoritesFragmentViewModel::class.java)
        favoritesFragmentViewModel.posts.observe(viewLifecycleOwner, this::consumeState)
    }

    private fun consumeState(state: FavoritesStateMachine.State) {
        adapter.items = state.favoritesPosts
    }

    companion object {
        fun newInstance() = FavoritesPostsFragment()
    }
}
