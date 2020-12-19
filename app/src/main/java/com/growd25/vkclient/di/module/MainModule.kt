package com.growd25.vkclient.di.module

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.growd25.vkclient.data.db.DataBase
import com.growd25.vkclient.data.net.PostsInterceptor
import com.growd25.vkclient.data.net.VkApi
import com.growd25.vkclient.App
import com.growd25.vkclient.data.db.dao.PostDao
import com.growd25.vkclient.data.db.dao.ProfileDao
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class MainModule {

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideRetrofitService(retrofit: Retrofit): VkApi =
        retrofit.create(VkApi::class.java)

    @Provides
    fun provideOkHttpClient(postsInterceptor: PostsInterceptor) =
        OkHttpClient().newBuilder()
            .addInterceptor(postsInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()

    @Provides
    @Singleton
    fun provideConnectivityManager(app: App): ConnectivityManager =
        app.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    fun provideNetWorkRequest(): NetworkRequest = NetworkRequest.Builder().build()

    @Provides
    fun provideRetrofit(postsInterceptor: PostsInterceptor): Retrofit = Retrofit.Builder()
        .client(provideOkHttpClient(postsInterceptor))
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(VkApi.BASE_URL)
        .build()

    @Provides
    @Singleton
    fun provideDataBase(app: App) =
        Room.databaseBuilder(app.applicationContext, DataBase::class.java, DataBase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePostsDao(dataBase: DataBase): PostDao = dataBase.postDao


    @Provides
    fun provideProfileDao(dataBase: DataBase): ProfileDao = dataBase.profileDao

}
