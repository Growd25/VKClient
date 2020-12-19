package com.growd25.vkclient.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String,
    val domain: String,
    val photo: String?,
    val about: String,
    val bdate: String,
    val city: String?,
    val county: String?,
    val career: String?,
    val education: String?,
    val followers_count: Int,
    val last_seen: Int,
)
