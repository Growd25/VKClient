package com.growd25.vkclient.di

import com.growd25.vkclient.di.module.MainModule
import com.growd25.vkclient.App
import com.growd25.vkclient.di.module.AppModule
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, MainModule::class])
interface ApplicationComponent : AndroidInjector<App> {

    @Component.Factory
    interface Factory : AndroidInjector.Factory<App>

}
