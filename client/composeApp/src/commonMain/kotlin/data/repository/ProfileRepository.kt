package data.repository

import data.utils.ContentBasedPersistentStorage
import data.utils.getValue
import data.utils.setValue
import kotlinx.serialization.Serializable


@Serializable
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