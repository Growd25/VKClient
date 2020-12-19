package com.growd25.vkclient.di.module

import com.growd25.vkclient.features.createpost.CreatePostFragment
import com.growd25.vkclient.features.favorites.FavoritesPostsFragment
import com.growd25.vkclient.features.feedcontainer.FeedContainerFragment
import com.growd25.vkclient.features.news.ui.NewsFragment
import com.growd25.homework.features.postdetails.PostDetailsFragment
import com.growd25.vkclient.MainActivity
import com.growd25.vkclient.features.profile.ui.ProfileFragment
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector

@Module(includes = [AndroidInjectionModule::class])
abstract class AppModule {

    @ContributesAndroidInjector
    abstract fun contributesPostFragment(): NewsFragment

    @ContributesAndroidInjector
    abstract fun contributesDetailsFragment(): PostDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributesFavoritesPostsFragment(): FavoritesPostsFragment

    @ContributesAndroidInjector
    abstract fun contributesPostsFragmentContainer(): FeedContainerFragment

    @ContributesAndroidInjector
    abstract fun contributesMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributesProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributesCreateFragment(): CreatePostFragment
}
