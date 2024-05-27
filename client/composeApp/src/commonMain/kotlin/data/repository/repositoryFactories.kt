package data.repository

import data.mock.MockFeedRepository
import data.service.FreshApi

interface RepositoryFactory {
    
    fun createFeedRepository() : FeedRepository

    fun createProfileRepository() : ProfileRepository

}

class RepositoryFactoryImpl(
    val api: FreshApi
) : RepositoryFactory {

    override fun createFeedRepository(): FeedRepository {
        return FeedRepositoryImpl(api)
    }

    override fun createProfileRepository(): ProfileRepository {
        return ProfileRepositoryImpl()
    }

}