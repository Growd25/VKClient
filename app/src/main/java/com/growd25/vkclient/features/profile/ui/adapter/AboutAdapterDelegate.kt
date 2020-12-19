package com.growd25.vkclient.features.profile.ui.adapter

import android.annotation.SuppressLint
import com.growd25.vkclient.R
import com.growd25.vkclient.features.profile.ui.viewitem.AboutViewItem
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_about.*

@SuppressLint("SetTextI18n")
fun aboutAdapterDelegate() =
    adapterDelegateLayoutContainer<AboutViewItem, Any>(R.layout.item_about) {
        bind {
            valueText.text = "${containerView.resources.getString(item.descriptionStringResId)}: ${item.text}"
            iconImage.setImageResource(item.iconResId)
        }
    }
