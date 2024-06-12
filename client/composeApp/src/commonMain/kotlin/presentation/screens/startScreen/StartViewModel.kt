package presentation.screens.startScreen

import data.repository.JokeRepository
import domain.model.JokeModel
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.model.Resource

class StartViewModel(
    val jokeRepository: JokeRepository,
) : BaseViewModel<StartViewModel.State>() {

    data class State(
        val jokes: List<JokeModel> = emptyList(),
        val readyState: Resource<Unit> = IdleResource,
    )

    init {
        fetchJokes()
    }

    fun fetchJokes() {
        viewModelScope.launch {
            try {
                reduce { copy(readyState = LoadingResource) }
                val jokes = jokeRepository.fetchJokes()
                reduce { copy(
                    jokes = jokes,
                    readyState = CompleteResource(Unit)
                ) }
            } catch (e: Throwable) {
                state.value = state.value.copy(readyState = ExceptionResource(e))
                e.printStackTrace()
            }
        }
    }

    override fun onInitState(): State = State()

}