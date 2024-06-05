package data.repository

import data.service.MainApi
import data.utils.ProfileStorage

interface RepositoryFactory {
    
//    fun createFeedRepository() : FeedRepository

    fun createProfileRepository() : ProfileRepository

}

class RepositoryFactoryImpl(
    val api: MainApi,
    val profileStorage: ProfileStorage,
) : RepositoryFactory {

//    override fun createFeedRepository(): FeedRepository {
//        return FeedRepositoryImpl(api)
//    }

    override fun createProfileRepository(): ProfileRepository {
        return ProfileRepositoryImpl(profileStorage)
    }

}