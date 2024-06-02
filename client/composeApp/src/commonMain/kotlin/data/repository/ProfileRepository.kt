package data.repository

import domain.model.Profile
import data.utils.ContentBasedPersistentStorage
import data.utils.getValue
import data.utils.setValue


interface ProfileRepository {

    fun fetchProfileLocal() : Profile?

    fun saveProfileLocal(profile: Profile)

}

class ProfileRepositoryImpl(
    private val storage: ContentBasedPersistentStorage,
) : ProfileRepository {

    private var profile: Profile? by storage

    init {
        profile = Profile(
            email = "temp",
            token = "123",
        )
    }

    override fun fetchProfileLocal(): Profile? {
        return profile.apply {
            println("mylog Profile property: ${profile}")
        }
    }

    override fun saveProfileLocal(profile: Profile) {
        this.profile = profile
    }
}