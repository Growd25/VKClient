package com.growd25.vkclient.features.news.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.growd25.vkclient.features.global.adapter.PostAdapter
import com.growd25.vkclient.features.global.model.ErrorViewItem
import com.growd25.vkclient.features.global.model.LoadingViewItem
import com.growd25.vkclient.features.global.model.PaginatorState
import com.growd25.vkclient.features.global.model.PostItem
import com.growd25.homework.features.news.presentation.NewsStateMachine
import com.growd25.homework.features.news.presentation.NewsViewModel
import com.growd25.homework.features.news.ui.NewsViewModelFactory
import com.growd25.vkclient.R
import com.growd25.vkclient.utils.PaginationAdapterHelper
import com.growd25.vkclient.utils.visibility
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_news_posts.*
import kotlinx.android.synthetic.main.posts_shimmer_container.*
import javax.inject.Inject

class NewsFragment : Fragment(R.layout.fragment_news_posts) {

    @Inject
    lateinit var viewModelFactory: NewsViewModelFactory

    private val viewModel: NewsViewModel by viewModels { viewModelFactory }

    private var newsPostsListener: NewsPostsListener? = null

    private val postAdapter = PostAdapter(
        onPostItemClicked = { postItem -> newsPostsListener?.onNewsPostItemClicked(postItem) },
        onFavoritesButtonClicked = { postItem ->
            viewModel.acceptAction(NewsStateMachine.Action.OnPostItemFavoriteClicked(postItem))
        },
        onRetryClicked = {
            viewModel.acceptAction(NewsStateMachine.Action.OnRetryFetchNewPagePostsClicked)
        },
        paginationAdapterHelper = PaginationAdapterHelper {
            viewModel.acceptAction(NewsStateMachine.Action.OnScrolledToEnd)
        }
    )

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        newsPostsListener = (context as NewsPostsListener)
    }

    override fun onDetach() {
        super.onDetach()
        newsPostsListener = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postRecyclerView.adapter = postAdapter
        postRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        viewModel.posts.observe(viewLifecycleOwner, this::consumeState)
        postSwipeRefreshLayout.setOnRefreshListener {
            viewModel.acceptAction(NewsStateMachine.Action.OnRefresh)
        }
        retryButton.setOnClickListener {
            viewModel.acceptAction(NewsStateMachine.Action.OnRetryFetchPostsClicked)
        }
    }

    private fun consumeState(state: NewsStateMachine.State) {
        when (state) {
            is NewsStateMachine.State.Loaded -> {
                postAdapter.items = when (state.paginatorState) {
                    PaginatorState.LOADED -> state.posts
                    PaginatorState.LOADING -> state.posts + LoadingViewItem
                    PaginatorState.ERROR -> state.posts + ErrorViewItem
                }
                postSwipeRefreshLayout.visibility(state.posts.isNotEmpty())
                postSwipeRefreshLayout.isRefreshing = state.isRefreshing
                shimmerFrameLayout.visibility(false)
                errorGroup.visibility(false)
            }
            NewsStateMachine.State.Error -> {
                postSwipeRefreshLayout.visibility(false)
                errorGroup.visibility(true)
                shimmerFrameLayout.visibility(false)
            }

            NewsStateMachine.State.Loading -> {
                postSwipeRefreshLayout.visibility(false)
                errorGroup.visibility(false)
                shimmerFrameLayout.visibility(true)
            }
        }
    }

    interface NewsPostsListener {
        fun onNewsPostItemClicked(postItem: PostItem)
    }

    companion object {
        fun newInstance() = NewsFragment()
    }
}
