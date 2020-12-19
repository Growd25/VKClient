package com.growd25.vkclient.data.net

import com.growd25.vkclient.data.net.model.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface VkApi {

    @GET("/method/newsfeed.get")
    fun getNewsFeed(
        @Query("filters") filters: String = NEWS_FEED_POST_PARAM,
        @Query("start_from") startFrom: String? = null,
        @Query("count") count: Int = NEWS_FEED_COUNT_PARAM
    ): Single<NewsFeedResponse>

    @GET("/method/likes.add")
    fun addFavotite(
        @Query("type") type: String?,
        @Query("owner_id") ownerId: Int?,
        @Query("item_id") item_id: Int?,
    ): Single<LikesResponse>

    @GET("/method/wall.createComment")
    fun createCommentary(
        @Query("post_id") postId: Int,
        @Query("message") message: String,
        @Query("owner_id") ownerId: Int,
    ): Single<CommentaryResponse>

    @GET("/method/wall.getComments")
    fun getCommentsFromNewsFeed(
        @Query("owner_id") ownerId: Int,
        @Query("post_id") postId: Int,
        @Query("extended") extended: Int = EXTENDED_PARAM,
    ): Single<PostDetailsResponse>

    @GET("method/likes.delete")
    fun deleteFavorite(
        @Query("type") type: String?,
        @Query("owner_id") ownerId: Int?,
        @Query("item_id") item_id: Int?,
    ): Single<LikesResponse>

    @GET("method/wall.post")
    fun createPost(
        @Query("message") message: String
    ): Single<CreatePostResponse>

    @GET("method/wall.getById")
    fun getPostById(
        @Query("posts") param: String,
        @Query("extended") extended: Int = EXTENDED_PARAM
    ): Single<ProfilePostsResponse>

    @GET("/method/wall.get")
    fun getPostsForProfile(
        @Query("filter") filter: String = QUERY_PARAM_OWNER,
        @Query("extended") extended: Int = EXTENDED_PARAM
    ): Single<ProfilePostsResponse>

    @GET("/method/users.get")
    fun getProfile(
        @Query("fields") fields: String = DEFAULT_PROFILE_FIELDS
    ): Single<ProfileResponse>

    companion object {
        const val BASE_URL = "https://api.vk.com"
        private const val EXTENDED_PARAM = 1
        private const val NEWS_FEED_POST_PARAM = "post"
        private const val NEWS_FEED_COUNT_PARAM = 20
        private const val DEFAULT_PROFILE_FIELDS: String =
            "domain,photo_200,about,bdate,city,country,career,education,followers_count,last_seen"
        private const val QUERY_PARAM_OWNER = "owner"
    }
}
