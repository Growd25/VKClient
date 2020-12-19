package com.growd25.vkclient

import android.app.Application
import com.growd25.vkclient.di.DaggerApplicationComponent
import com.vk.api.sdk.VK
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class App : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        VK.initialize(this)
        DaggerApplicationComponent.factory().create(this).inject(this)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }
}
