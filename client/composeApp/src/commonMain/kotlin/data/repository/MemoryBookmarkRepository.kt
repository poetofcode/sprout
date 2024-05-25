package data.repository

import domain.model.PostModel

class MemoryBookmarkRepository : BookmarkRepository {

    //private val posts: ArrayList<PostModel> = arrayListOf()
    //private val posts: MutableList<PostModel> = mutableListOf()
    private var posts: MutableList<PostModel> = mutableListOf()

    override fun add(post: PostModel) {
        posts.add(post)
    }

    override fun remove(id: String): List<PostModel> {
//        posts.removeIf{
//            it.id == id
//        }
//        return posts

        val updPosts = posts.filter {
            it.id != id
        }
        posts = updPosts.toMutableList()
        return posts
    }

    override fun getAll(): List<PostModel> = posts

}