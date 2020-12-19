package com.growd25.vkclient.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.growd25.vkclient.data.db.entity.ProfileEntity

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class ProfileDao {

    @Query("SELECT * FROM profile")
    abstract fun getProfileFlow(): Flowable<ProfileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(profileEntity: ProfileEntity): Completable

    @Query("SELECT id FROM profile")
    abstract fun getProfileId(): Single<List<Int>>
}
