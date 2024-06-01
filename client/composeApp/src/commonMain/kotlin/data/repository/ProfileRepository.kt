package data.repository

import data.utils.ContentBasedPersistentStorage
import data.utils.getValue
import data.utils.setValue


data class Profile(
    val token: String,
    val email: String,
)

interface ProfileRepository {

    fun fetchProfileLocal() : Profile?

    fun saveProfileLocal(profile: Profile)

}

class ProfileRepositoryImpl(
    private val storage: ContentBasedPersistentStorage,
) : ProfileRepository {

    private var profileProperty: Profile? by storage

    override fun fetchProfileLocal(): Profile? {
        return profileProperty
    }

    override fun saveProfileLocal(profile: Profile) {
        profileProperty = profile
    }
}