package com.growd25.homework.features.postdetails

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.growd25.vkclient.features.global.adapter.postAdapterDelegate
import com.growd25.vkclient.features.global.model.PostItem
import com.growd25.vkclient.features.postdetails.CommentaryViewItem
import com.growd25.vkclient.features.postdetails.commentaryAdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class PostDetailsAdapter : AsyncListDifferDelegationAdapter<Any>(DiffCallBack) {

    init {
        delegatesManager.addDelegate(postAdapterDelegate({}, {}))
        delegatesManager.addDelegate(commentaryAdapterDelegate())
    }

    object DiffCallBack : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return (
                    oldItem is CommentaryViewItem &&
                            newItem is CommentaryViewItem &&
                            oldItem.id == newItem.id &&
                            oldItem.fromId == newItem.fromId &&
                            oldItem.postId == newItem.postId
                    ) || (
                    oldItem is PostItem &&
                            newItem is PostItem &&
                            oldItem.id == newItem.id
                    )
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return (oldItem is CommentaryViewItem && newItem is CommentaryViewItem && oldItem == newItem) ||
                    (oldItem is PostItem && newItem is PostItem && oldItem == newItem)
        }
    }
}
