package data.repository

import data.utils.ProfileStorage
import domain.model.Profile


interface ProfileRepository {

    fun fetchProfileLocal() : Profile?

    fun saveProfileLocal(profile: Profile)

}

class ProfileRepositoryImpl(
    private val storage: ProfileStorage,
) : ProfileRepository {

//    init {
//        profile = Profile(
//            email = "temp",
//            token = "123",
//        )
//    }

    override fun fetchProfileLocal(): Profile? {
        return storage.load()
    }

    override fun saveProfileLocal(profile: Profile) {
        storage.save(profile)
    }
}