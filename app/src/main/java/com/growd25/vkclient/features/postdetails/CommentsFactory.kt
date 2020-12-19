package com.growd25.homework.features.postdetails

import com.growd25.vkclient.data.net.model.PostDetailsResponse
import com.growd25.vkclient.data.net.model.Profile
import com.growd25.vkclient.features.postdetails.CommentaryViewItem
import java.text.SimpleDateFormat
import java.util.*

object CommentsFactory {

    private val dateFormatter: SimpleDateFormat = SimpleDateFormat("H:m", Locale.US)

    fun toCommentViewItem(postDetailsResponse: PostDetailsResponse): List<CommentaryViewItem> {
        if (postDetailsResponse.response == null) return emptyList()
        val profiles = hashMapOf<Int, Profile>()
        val items = postDetailsResponse.response.items?.filter { !it.text.isNullOrBlank() }
        postDetailsResponse.response.profiles?.forEach { profile ->
            val profileId = profile.id
            if (profileId != null) profiles[profileId] = profile
        }

        return items?.map { item ->
            CommentaryViewItem(
                id = item.id,
                fromId = item.from_id,
                postId = item.post_id,
                groupImageUrl = profiles[item.from_id]?.photo_100,
                groupName = "${profiles[item.from_id]?.first_name} ${profiles[item.from_id]?.last_name}",
                date = item.date?.let { dateFormatter.format(it.toLong()) },
                commentaryText = item.text
            )
        }.orEmpty()
    }
}
