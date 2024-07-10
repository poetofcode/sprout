package data.repository

import data.entity.CreateSessionRequestBody
import data.service.MainApi
import data.utils.ProfileStorage
import domain.model.Notification
import domain.model.Profile


interface ProfileRepository {

    fun fetchProfileLocal(): Profile?

    fun saveProfileLocal(profile: Profile)

    fun clearProfileLocal()

    suspend fun createSession(email: String, password: String): Profile

    suspend fun deleteSession()

    suspend fun createSubscription()

    suspend fun deleteSubscription()

    suspend fun isSubscribed() : Boolean

    suspend fun fetchNotifications() : List<Notification>

}

class ProfileRepositoryImpl(
    private val api: MainApi,
    private val storage: ProfileStorage,
) : ProfileRepository {

    override fun fetchProfileLocal(): Profile? {
        return storage.load()
    }

    override fun saveProfileLocal(profile: Profile) {
        storage.save(profile)
    }

    override fun clearProfileLocal() {
        storage.clear()
    }


    override suspend fun createSession(email: String, password: String): Profile {
        return api.createSession(CreateSessionRequestBody(email, password))
            .resultOrError()
            .let { response ->
                Profile(
                    token = response.token,
                    email = response.user.login,
                )
            }
    }

    override suspend fun deleteSession() {
        storage.load()?.let {
            api.deleteSession(it.copy().token)
            storage.clear()
        }
    }

    override suspend fun createSubscription() {
        api.createSubscription()
    }

    override suspend fun deleteSubscription() {
        api.deleteSubscription()
    }

    override suspend fun isSubscribed(): Boolean {
        return api.getSubscription()
            .resultOrError()
            .isSubscribed
    }

    override suspend fun fetchNotifications(): List<Notification> {
        return api.getNotifications()
            .resultOrError()
            .items
            .map { dto ->
                Notification(
                    id = dto.id,
                    createdAt = dto.createdAt,
                    title = dto.title,
                    text = dto.text,
                    image = dto.image,
                    linkId = dto.linkId,
                    extras = dto.extras,
                )
            }
    }

}