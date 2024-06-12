package presentation.screens.startScreen

import domain.model.JokeModel
import presentation.base.BaseViewModel
import presentation.model.IdleResource
import presentation.model.Resource

class StartViewModel(
    /* val feedRepository: FeedRepository, */
) : BaseViewModel<StartViewModel.State>() {

    data class State(
        val posts: List<JokeModel> = emptyList(),
        val readyState: Resource<Unit> = IdleResource,
    )

    init {
        // fetchFeed()
    }

    /*
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

     */

    fun updatePostFavorite(id: String, isFavorite: Boolean) {
        var updatedPost: JokeModel? = null
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

    override fun onInitState(): State = State()

}