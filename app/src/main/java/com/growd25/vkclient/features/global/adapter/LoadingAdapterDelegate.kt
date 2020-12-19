package com.growd25.vkclient.features.global.adapter

import com.growd25.vkclient.features.global.model.LoadingViewItem
import com.growd25.vkclient.R
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer

fun loadingAdapterDelegate() =
    adapterDelegateLayoutContainer<LoadingViewItem, Any>(R.layout.item_loading) {

    }
