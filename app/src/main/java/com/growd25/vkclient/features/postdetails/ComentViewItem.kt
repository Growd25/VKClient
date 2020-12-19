package com.growd25.vkclient.features.postdetails

import com.bumptech.glide.Glide
import com.growd25.vkclient.R
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_coment.*
import kotlinx.android.synthetic.main.item_details.groupImageView
import kotlinx.android.synthetic.main.item_details.titleTextView

fun commentaryAdapterDelegate() =
    adapterDelegateLayoutContainer<CommentaryViewItem, Any>(R.layout.item_coment) {

        bind {
            comentDateTextView.text = item.date
            Glide.with(context).load(item.groupImageUrl).circleCrop().into(groupImageView)
            titleTextView.text = item.groupName
            subTitleTextView.text = item.commentaryText
        }
    }

data class CommentaryViewItem(
    val id: Int?,
    val postId: Int?,
    val fromId: Int?,
    val groupImageUrl: String?,
    val groupName: String?,
    val date: String?,
    val commentaryText: String?,
)
