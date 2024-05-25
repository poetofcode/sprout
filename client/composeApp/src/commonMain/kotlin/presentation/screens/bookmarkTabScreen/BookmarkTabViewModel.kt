package presentation.screens.bookmarkTabScreen

import androidx.compose.runtime.mutableStateOf
import data.repository.BookmarkRepository
import domain.model.PostModel
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.model.Resource

class BookmarkTabViewModel(val bookmarkRepository: BookmarkRepository)
    : BaseViewModel() {

    data class State(
        val posts: List<PostModel> = emptyList(),
        //val readyState: Resource<Unit> = IdleResource,
    )

    val state = mutableStateOf(State())

//    init {
//        fetchBookmarks()
//    }

    fun fetchBookmarks() {
        val posts = bookmarkRepository.getAll()
        state.value = state.value.copy(posts = posts)
    }

    fun removeBookmark(id: String) {
        println("1 >>>>>>>>>")
        for (post in state.value.posts) {
            println("$post")
        }

        val updatedPosts = bookmarkRepository.remove(id)

        println("2 >>>>>>>>>")
        for (post in updatedPosts) {
            println("$post")
        }

        state.value = state.value.copy(posts = updatedPosts);
    }

}