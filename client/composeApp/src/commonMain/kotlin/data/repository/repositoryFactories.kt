package data.repository

import data.mock.MockFeedRepository
import data.service.FreshApi

interface RepositoryFactory {
    
    fun createFeedRepository() : FeedRepository
    fun createBookmarkRepository() : BookmarkRepository
    
}

class MockRepositoryFactory : RepositoryFactory {
    override fun createFeedRepository(): FeedRepository = MockFeedRepository()
    override fun createBookmarkRepository(): BookmarkRepository = MemoryBookmarkRepository()

}

class RepositoryFactoryImpl(
    val api: FreshApi
) : RepositoryFactory {

    override fun createFeedRepository(): FeedRepository {
        return FeedRepositoryImpl(api)
    }

    override fun createBookmarkRepository(): BookmarkRepository = MemoryBookmarkRepository()

}