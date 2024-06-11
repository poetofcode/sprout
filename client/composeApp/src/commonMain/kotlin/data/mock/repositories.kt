package data.mock

import data.repository.FeedRepository
import domain.model.PostModel

class MockFeedRepository : FeedRepository {
    
    override suspend fun fetchFeed(): List<PostModel> {
        return listOf(
            PostModel(
                title = "Чародейка чисел: Как Ада Лавлейс написала первый в мире программный код",
                image = "https://habrastorage.org/r/w1560/getpro/habr/upload_files/b20/ffb/a01/b20ffba015ba65b2a72792762c062028.png",
                link = "https://habr.com/ru/companies/psb/articles/798739/",
                commentsCount = "3"
            ),
            PostModel(
                title = "Странные осцилляции в казалось бы простой числовой последовательности",
                image = "https://habrastorage.org/r/w1560/getpro/habr/upload_files/731/62a/233/73162a23305ee4bb339cf4d11b4c8154.png",
                link = "https://habr.com/ru/articles/798733/",
                commentsCount = "3"
            ),
            PostModel(
                title = "Может ли тёмная материя состоять из гравитонов?",
                image = "https://habrastorage.org/r/w780/getpro/habr/upload_files/8e6/27b/bc6/8e627bbc650b18c23b1b1c2cf44bbfec.jpg",
                link = "https://habr.com/ru/articles/798713/",
                commentsCount = "3"
            ),
        ) 
    }

}