package com.growd25.vkclient.features.profile.ui.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.growd25.vkclient.features.global.adapter.postAdapterDelegate
import com.growd25.vkclient.features.global.model.PostItem
import com.growd25.vkclient.features.profile.ui.viewitem.AboutViewItem
import com.growd25.vkclient.features.profile.ui.viewitem.CreatePostViewItem
import com.growd25.vkclient.features.profile.ui.viewitem.ProfileHeaderViewItem
import com.growd25.vkclient.utils.PaginationAdapterHelper
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class ProfileAdapter(
    onPostItemClicked: (PostItem) -> Unit,
    onCreatePostButtonClicked: (() -> Unit),
    onFavoritesButtonClicked: (PostItem) -> Unit,
    private val paginationAdapterHelper: PaginationAdapterHelper? = null
) : AsyncListDifferDelegationAdapter<Any>(DiffCallBack) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        paginationAdapterHelper?.onBind(position, itemCount)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any?>) {
        super.onBindViewHolder(holder, position, payloads)
        paginationAdapterHelper?.onBind(position, itemCount)
    }

    init {
        delegatesManager.addDelegate(profileHeaderAdapterDelegate())
        delegatesManager.addDelegate(aboutAdapterDelegate())
        delegatesManager.addDelegate(postAdapterDelegate (onPostItemClicked, onFavoritesButtonClicked))
        delegatesManager.addDelegate(createPostAdapterDelegate(onCreatePostButtonClicked))
    }

    object DiffCallBack : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return (oldItem is AboutViewItem && newItem is AboutViewItem && oldItem.id == newItem.id)
                    || (oldItem is PostItem && newItem is PostItem && oldItem.id == newItem.id) ||
                    (oldItem is ProfileHeaderViewItem && newItem is ProfileHeaderViewItem && oldItem.id == newItem.id) ||
                    (oldItem is CreatePostViewItem && newItem is CreatePostViewItem)

        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return (oldItem is AboutViewItem && newItem is AboutViewItem && oldItem == newItem) ||
                    (oldItem is PostItem && newItem is PostItem && oldItem == newItem) ||
                    (oldItem is ProfileHeaderViewItem && newItem is ProfileHeaderViewItem && oldItem == newItem) ||
                    (oldItem is CreatePostViewItem && newItem is CreatePostViewItem)
        }
    }
}
