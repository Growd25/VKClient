package com.growd25.vkclient.features.profile.ui.adapter

import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import com.growd25.vkclient.R
import com.growd25.vkclient.features.profile.ui.viewitem.ProfileHeaderViewItem
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_profile_header.*

@SuppressLint("SetTextI18n")
fun profileHeaderAdapterDelegate() =
    adapterDelegateLayoutContainer<ProfileHeaderViewItem, Any>(R.layout.item_profile_header) {
        bind {
            Glide.with(context).load(item.imageUrl).circleCrop().into(avatarImageView)
            nameText.text = "${item.firstName} ${item.lastName}"
            domainText.text = item.domain
            birthdayText.text = item.birthDate
            lastSeenText.text = getString(R.string.last_seen) + item.lastSeen
        }
    }
