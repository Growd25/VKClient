package com.growd25.vkclient.features.global.adapter

import com.growd25.vkclient.features.global.model.LoadingViewItem
import com.growd25.vkclient.R
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_error.*

fun errorAdapterDelegate(onRetryClicked: () -> Unit) =
    adapterDelegateLayoutContainer<LoadingViewItem, Any>(R.layout.item_error) {
        retryButton.setOnClickListener { onRetryClicked() }
    }
