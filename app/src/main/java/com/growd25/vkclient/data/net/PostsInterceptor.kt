package com.growd25.vkclient.data.net

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostsInterceptor @Inject constructor() : Interceptor {

    var token: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request()
            .url.newBuilder()
            .addQueryParameter(ACCESS_TOKEN_PARAM, token)
            .addQueryParameter(VK_SDK_VERSION_NAME, VK_SDK_VERSION_VALUE)
            .build()

        val request = chain.request()
            .newBuilder()
            .url(url)
            .build()
        return chain.proceed(request)
    }

    companion object {
        private const val ACCESS_TOKEN_PARAM = "access_token"
        private const val VK_SDK_VERSION_NAME = "v"
        private const val VK_SDK_VERSION_VALUE = "5.124"
    }
}
