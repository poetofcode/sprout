package data.repository

import data.service.FreshApi
import data.utils.ContentBasedPersistentStorage
import data.utils.ProfileStorage

interface RepositoryFactory {
    
    fun createFeedRepository() : FeedRepository

    fun createProfileRepository() : ProfileRepository

}

class RepositoryFactoryImpl(
    val api: FreshApi,
    val profileStorage: ProfileStorage,
) : RepositoryFactory {

    override fun createFeedRepository(): FeedRepository {
        return FeedRepositoryImpl(api)
    }

    override fun createProfileRepository(): ProfileRepository {
        return ProfileRepositoryImpl(profileStorage)
    }

}