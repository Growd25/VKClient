package com.growd25.vkclient.utils

class PaginationAdapterHelper(
    private val onLoadMoreCallback: (offset: Int) -> Unit
) {

    fun onBind(adapterPosition: Int, totalItemCount: Int) {
        if (adapterPosition > totalItemCount - DEFAULT_LOAD_MORE_SUBSTITUTIONS) {
            onLoadMoreCallback(adapterPosition)
        }
    }

    companion object {
        private const val DEFAULT_LOAD_MORE_SUBSTITUTIONS = 2
    }
}
