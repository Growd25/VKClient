package com.growd25.vkclient.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.growd25.vkclient.data.db.dao.PostDao
import com.growd25.vkclient.data.db.dao.ProfileDao
import com.growd25.vkclient.data.db.entity.PostEntity
import com.growd25.vkclient.data.db.entity.ProfileEntity

@Database(
    entities = [
        PostEntity::class,
        ProfileEntity::class
    ],
    version = 4
)
abstract class DataBase : RoomDatabase() {
    abstract val postDao: PostDao
    abstract val profileDao: ProfileDao

    companion object {
        const val DATABASE_NAME = "db.db"
    }
}
