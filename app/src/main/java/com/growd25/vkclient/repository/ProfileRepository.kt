package com.growd25.vkclient.repository

import com.growd25.vkclient.data.net.VkApi
import com.growd25.vkclient.data.net.model.ProfileItemResponse
import com.growd25.vkclient.data.db.dao.ProfileDao
import com.growd25.vkclient.data.db.entity.ProfileEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: VkApi,
    private val dao: ProfileDao,
) {

    fun loadProfile(): Single<ProfileEntity> {
        return api.getProfile()
            .map { response ->
                response.response?.firstOrNull()?.toProfileEntity() ?: error("failed map profile")
            }
            .subscribeOn(Schedulers.io())
    }

    fun getProfileId(): Single<List<Int>> = dao.getProfileId()
        .subscribeOn(Schedulers.io())

    fun getProfileFlow(): Flowable<ProfileEntity> {
        return dao.getProfileFlow()
    }

    fun insertProfile(profileEntity: ProfileEntity): Completable {
        return dao.insert(profileEntity)
    }

    private fun ProfileItemResponse.toProfileEntity(): ProfileEntity? {
        return ProfileEntity(
            id = id,
            firstName = first_name ?: return null,
            lastName = last_name ?: return null,
            domain = domain,
            photo = photo_200,
            about = about,
            bdate = bdate,
            city = city.title,
            county = country?.title,
            career = career?.first()?.position,
            education = university_name,
            followers_count = 0,
            last_seen = last_seen?.time ?: return null
        )
    }
}
