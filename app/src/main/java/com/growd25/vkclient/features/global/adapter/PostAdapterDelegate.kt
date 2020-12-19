package com.growd25.vkclient.features.global.adapter

import android.view.ViewGroup
import com.growd25.vkclient.features.global.model.PostItem
import com.growd25.homework.features.global.views.PostView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer

fun postAdapterDelegate(
    onPostItemClicked: (PostItem) -> Unit,
    onFavoritesButtonClicked: (PostItem) -> Unit
): AdapterDelegate<List<Any>> = adapterDelegateLayoutContainer<PostItem, Any>(
    layout = 0,
    layoutInflater = { parent, layoutRes ->
        PostView(parent.context).apply {
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    },
    block = {
        (containerView as PostView).let { photoPostView ->
            photoPostView.setOnClickListener { onPostItemClicked(item) }
            photoPostView.onFavoritesButtonClicked = { onFavoritesButtonClicked(item) }
        }
        bind { (containerView as PostView).bind(item) }
    }
)
