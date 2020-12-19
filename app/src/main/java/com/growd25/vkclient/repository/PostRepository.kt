package com.growd25.vkclient.repository

import com.growd25.vkclient.data.net.VkApi
import com.growd25.vkclient.features.global.model.PostItem
import com.growd25.vkclient.features.global.model.PostItemImage
import com.growd25.vkclient.data.db.dao.PostDao
import com.growd25.vkclient.data.db.entity.PostEntity
import com.growd25.vkclient.data.db.entity.PostSource
import com.growd25.vkclient.data.net.model.*
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue

class PostRepository @Inject constructor(
    private val api: VkApi,
    private val postDao: PostDao
) {

    fun fetchNewsPosts(startFrom: String? = null): Single<String> =
        api.getNewsFeed(startFrom = startFrom)
            .flatMap { response ->
                val nextFrom = response.response?.next_from
                when {
                    nextFrom == null -> {
                        Single.error(IllegalStateException("next_from field is null"))
                    }
                    startFrom == null -> {
                        postDao.replacePosts(PostSource.NEWS, response.toPostEntities())
                            .toSingleDefault(nextFrom)
                    }
                    else -> {
                        postDao.insertPosts(response.toPostEntities())
                            .toSingleDefault(nextFrom)
                    }
                }
            }
            .subscribeOn(Schedulers.io())

    fun fetchWallPosts(): Completable =
        api.getPostsForProfile()
            .flatMapCompletable { response ->
                postDao.replacePosts(PostSource.WALL, response.toPostEntities())
            }
            .subscribeOn(Schedulers.io())


    fun getNewsPostsFlow() = postDao.getNewsPostsFlow()
        .map { posts -> posts.map { post -> post.toPostItem() } }
        .subscribeOn(Schedulers.io())


    fun getFavoritesNewsPostsFlow() = postDao.getFavoritesNewsPostsFlow()
        .map { posts -> posts.map { post -> post.toPostItem() } }
        .subscribeOn(Schedulers.io())

    fun getFavoritesPostsCountFlow() = postDao.getFavoritesPostsCountFlow()
        .subscribeOn(Schedulers.io())

    fun getWallPostsFlow() = postDao.getWallPostsFlow()
        .map { posts -> posts.map { post -> post.toPostItem() } }
        .subscribeOn(Schedulers.io())

    fun toggleFavorite(postItem: PostItem): Completable =
        if (postItem.isFavorite) deleteFavorite(postItem) else addFavorite(postItem)

    private val dateFormatter: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)

    private fun addFavorite(postItem: PostItem) =
        postDao.updateFavoriteById(
            id = postItem.id,
            isFavorite = true,
            likesCount = postItem.likesCount + 1
        ).andThen {
            api.addFavotite(postItem.type, postItem.ownerId, postItem.itemId)
                .ignoreElement()
                .onErrorResumeNext {
                    postDao.updateFavoriteById(
                        id = postItem.id,
                        isFavorite = false,
                        likesCount = postItem.likesCount
                    )
                }
        }.subscribeOn(Schedulers.io())

    private fun deleteFavorite(postItem: PostItem) = postDao.updateFavoriteById(
        id = postItem.id,
        isFavorite = false,
        likesCount = postItem.likesCount - 1
    ).andThen {
        api.deleteFavorite(postItem.type, postItem.ownerId, postItem.itemId)
            .ignoreElement()
            .onErrorResumeNext {
                postDao.updateFavoriteById(
                    id = postItem.id,
                    isFavorite = true,
                    likesCount = postItem.likesCount
                )
            }
    }.subscribeOn(Schedulers.io())

    fun createPost(profileId: Int, message: String): Completable {
        return api.createPost(message)
            .flatMapCompletable {
                val postId = it.response?.post_id
                if (postId != null) {
                    api.getPostById("${profileId}_$postId")
                        .flatMapCompletable { response ->
                            val postEntity = response.toPostEntities().firstOrNull()
                            if (postEntity != null) {
                                postDao.insertPost(postEntity).subscribeOn(Schedulers.io())
                            } else {
                                Completable.complete()
                            }
                        }
                } else {
                    Completable.complete()
                }
            }
    }

    private fun NewsFeedResponse.toPostEntities(): List<PostEntity> =
        buildPostEntities(
            postSource = PostSource.NEWS,
            groups = response?.groups,
            profiles = response?.profiles,
            items = response?.items
        )

    private fun ProfilePostsResponse.toPostEntities(): List<PostEntity> =
        buildPostEntities(
            postSource = PostSource.WALL,
            groups = null,
            profiles = response?.profiles,
            items = response?.items
        )

    private fun buildPostEntities(
        postSource: PostSource,
        groups: List<Group>?,
        profiles: List<Profile>?,
        items: List<Post>?
    ): List<PostEntity> {
        val groupsMap: HashMap<Int, Group> = hashMapOf()
        val profilesMap: HashMap<Int, Profile> = hashMapOf()

        groups?.forEach { group ->
            group.id?.let { groupId -> groupsMap[groupId] = group }
        }
        profiles?.forEach { profile ->
            profile.id?.let { profileId -> profilesMap[profileId] = profile }
        }
        return items?.mapNotNull { item ->
            val id = item.id ?: item.post_id ?: return@mapNotNull null
            val sourceId = item.source_id ?: item.owner_id ?: return@mapNotNull null
            val type = item.post_type ?: return@mapNotNull null

            val groupImageUrl: String?
            val groupName: String?
            if (sourceId > 0) {
                val profile: Profile? = profilesMap[sourceId]
                groupImageUrl = profile?.photo_100
                groupName = profile?.let { "${it.first_name} ${it.last_name}" }
            } else {
                val group: Group? = groupsMap[sourceId.absoluteValue]
                groupImageUrl = group?.photo_200
                groupName = group?.name
            }
            val sizes: Sizes? = if (item.attachments != null) {
                getPhotoUrl(item.attachments)
            } else {
                getPhotoUrl(item.copy_history?.firstOrNull()?.attachments)
            }
            PostEntity(
                id = id,
                groupImageUrl = groupImageUrl,
                groupName = groupName,
                date = item.date ?: 0,
                postText = if (!item.text.isNullOrBlank()) item.text else null,
                likesCount = item.likes?.count ?: 0,
                type = type,
                ownerId = sourceId,
                itemId = item.post_id,
                isFavorite = item.likes?.user_likes == 1,
                postImageUrl = sizes?.url,
                postImageHeight = sizes?.height,
                postImageWidth = sizes?.width,
                commentaryCount = item.comments?.count,
                postSource = postSource.name
            )
        }.orEmpty()
    }

    private fun getPhotoUrl(attachments: List<Attachment>?): Sizes? {
        if (attachments != null && attachments.isNotEmpty()) {
            val attachmentsWithPhoto = attachments.filter { it.type == "photo" }
            if (attachmentsWithPhoto.isNotEmpty()) {
                return attachmentsWithPhoto.first().photo?.sizes?.maxByOrNull { it.width ?: 0 }
            }
        }
        return null
    }

    private fun PostEntity.toPostItem() = PostItem(
        id = id,
        groupImageUrl = groupImageUrl,
        groupName = groupName ?: "",
        date = dateFormatter.format(date.toLong()),
        postText = postText,
        postImage = toPostItemImage(),
        likesCount = likesCount,
        type = type,
        ownerId = ownerId,
        itemId = itemId,
        isFavorite = isFavorite,
        commentaryCount = commentaryCount ?: 0,
    )

    private fun PostEntity.toPostItemImage() =
        if (postImageHeight != null && postImageWidth != null && postImageUrl != null) {
            PostItemImage(
                postImageHeight = postImageHeight,
                postImageWidth = postImageWidth,
                postImageUrl = postImageUrl
            )
        } else {
            null
        }
}
