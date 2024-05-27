package data.repository


data class Profile(
    val token: String,
    val email: String,
)

interface ProfileRepository {

    fun fetchProfileLocal() : Profile?

    fun saveProfileLocal(profile: Profile)

}

class ProfileRepositoryImpl : ProfileRepository {
    override fun fetchProfileLocal(): Profile? {
        // TODO
        return null
    }

    override fun saveProfileLocal(profile: Profile) {
        // TODO
    }
}