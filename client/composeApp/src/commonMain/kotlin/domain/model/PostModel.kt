package domain.model

data class PostModel(
    var title: String,
    var image: String?,
    var link: String,
    var commentsCount: String,
    var isFavorite: Boolean = false
) {

    val id: String
        get() = link

}
