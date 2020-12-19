package com.growd25.vkclient.features.profile.ui.factory

import com.growd25.vkclient.R
import com.growd25.vkclient.data.db.entity.ProfileEntity
import com.growd25.vkclient.features.profile.ui.viewitem.AboutViewItem
import com.growd25.vkclient.features.profile.ui.viewitem.CreatePostViewItem
import com.growd25.vkclient.features.profile.ui.viewitem.ProfileHeaderViewItem
import java.text.SimpleDateFormat
import java.util.*

object ProfileViewItemFactory {

    private val dateFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    fun buildProfile(profileEntity: ProfileEntity?): List<Any> =
        mutableListOf<Any>().apply {
            if (profileEntity != null) {
                header(profileEntity)
                about(profileEntity)
                createPost()
            }
        }

    private fun MutableList<Any>.header(profileEntity: ProfileEntity) = apply {
        add(
            ProfileHeaderViewItem(
                id = profileEntity.id,
                imageUrl = profileEntity.photo,
                firstName = profileEntity.firstName,
                lastName = profileEntity.lastName,
                domain = profileEntity.domain,
                birthDate = profileEntity.bdate,
                lastSeen = dateFormatter.format(Date(profileEntity.last_seen.toLong()))
            )
        )
    }

    private fun MutableList<Any>.about(profileEntity: ProfileEntity): MutableList<Any> = apply {
        if (profileEntity.about.isNotBlank()) {
            add(
                AboutViewItem(
                    id = R.string.about_title_text,
                    iconResId = R.drawable.about_image,
                    descriptionStringResId = R.string.about_title_text,
                    text = profileEntity.about
                )
            )
        }
        if (!profileEntity.county.isNullOrBlank()) {
            add(
                AboutViewItem(
                    id = R.string.country_title_text,
                    iconResId = R.drawable.country_image,
                    descriptionStringResId = R.string.country_title_text,
                    text = profileEntity.county
                )
            )
        }
        if (!profileEntity.city.isNullOrBlank()) {
            add(
                AboutViewItem(
                    id = R.string.city_title_text,
                    iconResId = R.drawable.city_image,
                    descriptionStringResId = R.string.city_title_text,
                    text = profileEntity.city
                )
            )
        }
        if (!profileEntity.career.isNullOrBlank()) {
            add(
                AboutViewItem(
                    id = R.string.career_title_text,
                    iconResId = R.drawable.career_image,
                    descriptionStringResId = R.string.career_title_text,
                    text = profileEntity.career
                )
            )
        }
        if (!profileEntity.education.isNullOrBlank()) {
            add(
                AboutViewItem(
                    id = R.string.education_title_text,
                    iconResId = R.drawable.education_image,
                    descriptionStringResId = R.string.education_title_text,
                    text = profileEntity.education
                )
            )
        }
        if (profileEntity.followers_count != 0) {
            add(
                AboutViewItem(
                    id = R.string.followers_count_title_text,
                    iconResId = R.drawable.followers_count_image,
                    descriptionStringResId = R.string.followers_count_title_text,
                    text = profileEntity.followers_count.toString()
                )
            )
        }
    }

    private fun MutableList<Any>.createPost() = apply {
        add(CreatePostViewItem)
    }

}
