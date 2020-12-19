package com.growd25.vkclient.features.global.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.growd25.vkclient.features.global.model.ErrorViewItem
import com.growd25.vkclient.features.global.model.LoadingViewItem
import com.growd25.vkclient.features.global.model.PostItem
import com.growd25.vkclient.utils.PaginationAdapterHelper
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class PostAdapter(
    onPostItemClicked: (PostItem) -> Unit,
    onFavoritesButtonClicked: (PostItem) -> Unit,
    onRetryClicked: () -> Unit,
    private val paginationAdapterHelper: PaginationAdapterHelper? = null
) : AsyncListDifferDelegationAdapter<Any>(DiffCallBack) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        paginationAdapterHelper?.onBind(position, itemCount)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any?>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        paginationAdapterHelper?.onBind(position, itemCount)
    }

    init {
        delegatesManager.addDelegate(
            postAdapterDelegate(
                onPostItemClicked,
                onFavoritesButtonClicked
            )
        )
        delegatesManager.addDelegate(loadingAdapterDelegate())
        delegatesManager.addDelegate(errorAdapterDelegate(onRetryClicked))
    }

    object DiffCallBack : DiffUtil.ItemCallback<Any>() {

        private const val LIKES_CHANGED_PAYLOAD = 0

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return (oldItem is PostItem && newItem is PostItem && oldItem.id == newItem.id) ||
                    (oldItem is LoadingViewItem && newItem is LoadingViewItem) ||
                    (oldItem is ErrorViewItem && newItem is ErrorViewItem)
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return (oldItem is PostItem && newItem is PostItem && oldItem == newItem) ||
                    (oldItem is LoadingViewItem && newItem is LoadingViewItem) ||
                    (oldItem is ErrorViewItem && newItem is ErrorViewItem)
        }

        override fun getChangePayload(oldItem: Any, newItem: Any): Any? =
            LIKES_CHANGED_PAYLOAD.takeIf {
                oldItem is PostItem && newItem is PostItem && oldItem.isFavorite != newItem.isFavorite
            }
    }
}
