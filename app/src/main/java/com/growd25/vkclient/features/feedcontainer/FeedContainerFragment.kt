package com.growd25.vkclient.features.feedcontainer

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.growd25.vkclient.features.favorites.FavoritesPostsFragment
import com.growd25.vkclient.features.news.ui.NewsFragment
import com.growd25.vkclient.R
import com.growd25.vkclient.features.profile.ui.ProfileFragment
import com.growd25.vkclient.utils.addFragment
import com.growd25.vkclient.utils.attachDetachFragmentByTag
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_feed_container.*
import javax.inject.Inject

class FeedContainerFragment : Fragment(R.layout.fragment_feed_container) {

    @Inject
    lateinit var containerViewModelFactory: FeedContainerViewModelFactory

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel = ViewModelProvider(this, containerViewModelFactory).get(FeedContainerViewModel::class.java)
        val favoritesMenuItem: MenuItem = bottom_navigation.menu.findItem(R.id.favorites_menu_item)
        val newsMenuItem: MenuItem = bottom_navigation.menu.findItem(R.id.news_menu_item)
        viewModel.isFavoritesVisible.observe(viewLifecycleOwner) { isFavoritesVisible ->
            favoritesMenuItem.isVisible = isFavoritesVisible
            if (!isFavoritesVisible && childFragmentManager.findFragmentById(R.id.posts_fragments_container) is FavoritesPostsFragment) {
                attachDetachFragmentByTag(
                    containerId = R.id.posts_fragments_container,
                    tag = POST_FRAGMENT_TAG,
                    provideFragment = { NewsFragment.newInstance() }
                )
                newsMenuItem.isChecked = true
            }
        }

        if (childFragmentManager.findFragmentById(R.id.posts_fragments_container) == null) {
            addFragment(R.id.posts_fragments_container, NewsFragment.newInstance(), POST_FRAGMENT_TAG)
        }

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.news_menu_item -> {
                    attachDetachFragmentByTag(
                        containerId = R.id.posts_fragments_container,
                        tag = POST_FRAGMENT_TAG,
                        provideFragment = { NewsFragment.newInstance() }
                    )
                    true
                }

                R.id.favorites_menu_item -> {
                    attachDetachFragmentByTag(
                        containerId = R.id.posts_fragments_container,
                        tag = FAVORITES_POST_FRAGMENT_TAG,
                        provideFragment = { FavoritesPostsFragment.newInstance() }
                    )
                    true
                }
                R.id.profile_menu_item -> {
                    attachDetachFragmentByTag(
                        containerId = R.id.posts_fragments_container,
                        tag = PROFILE_FRAGMENT_TAG,
                        provideFragment = { ProfileFragment.newInstance() }
                    )
                    true
                }
                else -> false
            }
        }
    }

    companion object {
        private const val POST_FRAGMENT_TAG = "posts"
        private const val PROFILE_FRAGMENT_TAG = "profile"
        private const val FAVORITES_POST_FRAGMENT_TAG = "favorites"
        fun newInstance() = FeedContainerFragment()
    }

}
