package data.repository

import data.service.MainApi
import domain.model.PostModel

interface FeedRepository {

    suspend fun fetchFeed(): List<PostModel>

}

/*
class FeedRepositoryImpl(val api: MainApi) : FeedRepository {

    override suspend fun fetchFeed(): List<PostModel> {
        return api.fetchFeed()
            .resultOrError()
            .posts
            .orEmpty()
            .map { post ->
                PostModel(
                    title = post.title.orEmpty(),
                    image = post.image,
                    link = post.link.orEmpty(),
                    commentsCount = post.commentsCount ?: "0"
                )
            }
    }

}

 */