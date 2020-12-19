package com.growd25.vkclient

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.growd25.vkclient.data.net.PostsInterceptor
import com.growd25.vkclient.data.sharedpreferences.SharedPreferencesRepository
import com.growd25.vkclient.features.createpost.CreatePostFragment
import com.growd25.vkclient.features.feedcontainer.FeedContainerFragment
import com.growd25.vkclient.features.global.model.PostItem
import com.growd25.vkclient.features.news.ui.NewsFragment
import com.growd25.homework.features.postdetails.PostDetailsFragment
import com.growd25.vkclient.data.connectivity.NetworkStateRepository
import com.growd25.vkclient.features.profile.ui.ProfileFragment
import com.growd25.vkclient.utils.*
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKScope
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import com.vk.api.sdk.auth.VKAuthCallback as VKAuthCallback1

class MainActivity : AppCompatActivity(),
    NewsFragment.NewsPostsListener,
    ProfileFragment.CreatePostListener,
    CreatePostFragment.OnPostWriteListener {

    @Inject
    lateinit var interceptor: PostsInterceptor

    @Inject
    lateinit var networkStateRepository: NetworkStateRepository
    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var sharedPreferencesRepository: SharedPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_main)
        networkStateRepository.observeNetworkState()
            .subscribe(::networkIsAvailable)
            .connect(compositeDisposable)

        retryButton.setOnClickListener {
            errorGroup.visibility(false)
            VK.login(this, arrayListOf(VKScope.WALL, VKScope.FRIENDS, VKScope.OFFLINE))
        }
        if (sharedPreferencesRepository.tokenIsEmpty(TOKEN_SH_PF, TOKEN_SH_PF_KEY)) {
            VK.login(this, arrayListOf(VKScope.WALL, VKScope.FRIENDS))
        } else {
            if (savedInstanceState == null) {
                interceptor.token = sharedPreferencesRepository.getToken(TOKEN_SH_PF, TOKEN_SH_PF_KEY)
                supportFragmentManager.attachDetachFragmentByTag(
                    containerId = R.id.container,
                    tag = FEED_CONTAINER_FRAGMENT_TAG,
                    provideFragment = { FeedContainerFragment.newInstance() }
                )
            }
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun networkIsAvailable(onAvailable: Boolean) {
        if (!onAvailable) {
            Snackbar.make(container, resources.getString(R.string.network_is_not_available), Snackbar.LENGTH_LONG)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_CANCELED) {
            errorGroup.visibility(true)
        }
        val callback = object : VKAuthCallback1 {
            override fun onLogin(token: VKAccessToken) {
                sharedPreferencesRepository.saveToken(TOKEN_SH_PF, TOKEN_SH_PF_KEY, token.accessToken)
                interceptor.token = token.accessToken
                addFragment(R.id.container, FeedContainerFragment.newInstance())
            }

            override fun onLoginFailed(errorCode: Int) {
                errorGroup.visibility(true)
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onNewsPostItemClicked(postItem: PostItem) {
        replaceFragment(R.id.container, PostDetailsFragment.newInstance(postItem))
    }

    override fun onCreatePostButtonClick() {
        replaceFragment(R.id.container, CreatePostFragment.newInstance(), tag = CREATE_FRAGMENT_TAG)
    }


    override fun closeCreatePostFragment() {
        supportFragmentManager.popBackStack()
    }

    companion object {
        const val CREATE_FRAGMENT_TAG = "createFragment"
        private const val FEED_CONTAINER_FRAGMENT_TAG = "feedContainer"
        private const val TOKEN_SH_PF = "token"
        private const val TOKEN_SH_PF_KEY = "token_key"
    }
}
