package com.growd25.vkclient.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: Int,
    val groupImageUrl: String?,
    val groupName: String?,
    val date: Int,
    val postText: String?,
    val likesCount: Int,
    val commentaryCount: Int?,
    var type: String,
    var ownerId: Int,
    var itemId: Int?,
    var isFavorite: Boolean,
    val postImageUrl: String?,
    val postImageHeight: Int?,
    val postImageWidth: Int?,
    var postSource: String
)

enum class PostSource { NEWS, WALL }
