package com.growd25.vkclient.features.global.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostItem(
    val id: Int,
    val groupImageUrl: String?,
    val groupName: String,
    val date: String?,
    val postImage: PostItemImage?,
    val postText: String?,
    val likesCount: Int,
    var type: String,
    var ownerId: Int,
    var itemId: Int?,
    var isFavorite: Boolean,
    var commentaryCount: Int,
) : Parcelable

@Parcelize
data class PostItemImage(
    val postImageUrl: String,
    val postImageHeight: Int,
    val postImageWidth: Int,
) : Parcelable
