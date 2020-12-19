package com.growd25.vkclient.features.profile.ui.adapter

import com.growd25.vkclient.R
import com.growd25.vkclient.features.profile.ui.viewitem.CreatePostViewItem
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer

fun createPostAdapterDelegate(onClick: (() -> Unit)) =
    adapterDelegateLayoutContainer<CreatePostViewItem, Any>(R.layout.item_create_post) {
        itemView.setOnClickListener { onClick.invoke() }
    }
