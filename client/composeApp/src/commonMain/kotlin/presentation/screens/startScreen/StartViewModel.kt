package presentation.screens.startScreen

import androidx.compose.runtime.mutableStateOf
import data.repository.FeedRepository
import domain.model.PostModel
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.model.Resource

class StartViewModel(
    val feedRepository: FeedRepository,
) : BaseViewModel() {

    data class State(
        val posts: List<PostModel> = emptyList(),
        val readyState: Resource<Unit> = IdleResource,
    )

    val state = mutableStateOf(State())

    init {
        // fetchFeed()
    }

    fun fetchFeed() {
        viewModelScope.launch {
            try {
                state.value = state.value.copy(readyState = LoadingResource)
                state.value = state.value.copy(
                    posts = feedRepository.fetchFeed(),
                    readyState = CompleteResource(Unit)
                )
            } catch (e: Throwable) {
                state.value = state.value.copy(readyState = ExceptionResource(e))
                e.printStackTrace()
            }
        }
    }

    fun updatePostFavorite(id: String, isFavorite: Boolean) {
        var updatedPost: PostModel? = null
        val posts = state.value.posts.map {
            if (it.id == id) {
                updatedPost = it.copy(isFavorite = isFavorite)
                updatedPost!!
            }
            else it
        }

        state.value = state.value.copy(posts = posts)

        updatedPost?.let {

        }

    }

}