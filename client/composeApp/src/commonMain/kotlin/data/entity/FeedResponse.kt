package data.entity

import kotlinx.serialization.Serializable

@Serializable
data class FeedResponse(
    var result: String? = null,
    var posts: List<Post>? = null
) {

    @Serializable
    data class Post(
        var title: String? = null,
        var image: String? = null,
        var link: String? = null,
        var commentsCount: String? = null
    )
}
