package data.repository

import data.entity.CreateSessionRequestBody
import data.entity.TokenResponse
import data.service.MainApi
import data.utils.ProfileStorage
import domain.model.Profile


interface ProfileRepository {

    fun fetchProfileLocal(): Profile?

    fun saveProfileLocal(profile: Profile)

    suspend fun createSession(email: String, password: String): Profile

}

class ProfileRepositoryImpl(
    private val api:  MainApi,
    private val storage: ProfileStorage,
) : ProfileRepository {

    override fun fetchProfileLocal(): Profile? {
        return storage.load()
    }

    override fun saveProfileLocal(profile: Profile) {
        storage.save(profile)
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

}