package presentation.screens.startScreen

import data.repository.JokeRepository
import data.repository.ProfileRepository
import domain.model.JokeModel
import domain.model.Profile
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.model.Resource

class StartViewModel(
    val jokeRepository: JokeRepository,
    val profileRepository: ProfileRepository,
) : BaseViewModel<StartViewModel.State>() {

    data class State(
        val jokes: List<JokeModel> = emptyList(),
        val readyState: Resource<Unit> = IdleResource,
        val profile: Profile? = null,
    )

    init {
        fetchJokes()
        reduce {
            copy(
                profile = profileRepository.fetchProfileLocal()
            )
        }
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