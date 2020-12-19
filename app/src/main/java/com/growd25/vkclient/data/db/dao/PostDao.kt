package com.growd25.vkclient.data.db.dao

import androidx.room.*
import com.growd25.vkclient.data.db.entity.PostEntity
import com.growd25.vkclient.data.db.entity.PostSource
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPost(post: PostEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPosts(posts: List<PostEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPostsBlocking(posts: List<PostEntity>)

    @Query("SELECT * FROM posts WHERE postSource = 'NEWS' ORDER BY date DESC")
    abstract fun getNewsPostsFlow(): Flowable<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isFavorite = 1 AND postSource = 'NEWS' ORDER BY date DESC")
    abstract fun getFavoritesNewsPostsFlow(): Flowable<List<PostEntity>>

    @Query("SELECT COUNT(1) FROM posts WHERE isFavorite = 1 AND postSource = 'NEWS'")
    abstract fun getFavoritesPostsCountFlow(): Flowable<Int>

    @Query("SELECT * FROM posts WHERE postSource = 'WALL' ORDER BY date DESC")
    abstract fun getWallPostsFlow(): Flowable<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id")
    abstract fun getPostEntityById(id: Int): Single<PostEntity>

    @Query(
        """
        UPDATE posts
        SET isFavorite = :isFavorite,
            likesCount = :likesCount 
        WHERE id = :id
        """
    )
    abstract fun updateFavoriteById(
        id: Int,
        isFavorite: Boolean,
        likesCount: Int
    ): Completable

    @Query("DELETE FROM posts WHERE postSource = :postSource")
    abstract fun deletePosts(postSource: String)


    fun replacePosts(
        postSource: PostSource,
        posts: List<PostEntity>
    ) =
        Completable.fromAction { replacePostsBlocking(postSource, posts) }

    @Transaction
    open fun replacePostsBlocking(
        postSource: PostSource,
        posts: List<PostEntity>
    ) {
        deletePosts(postSource.name)
        insertPostsBlocking(posts)
    }
}
