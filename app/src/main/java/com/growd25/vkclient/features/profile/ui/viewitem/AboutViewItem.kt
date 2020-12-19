package com.growd25.vkclient.features.profile.ui.viewitem

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class AboutViewItem(
    val id: Int,
    @DrawableRes val iconResId: Int,
    @StringRes val descriptionStringResId: Int,
    val text: String
)
