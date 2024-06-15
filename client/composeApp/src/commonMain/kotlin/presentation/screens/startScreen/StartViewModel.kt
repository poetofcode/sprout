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
import presentation.model.shared.OnQuitProfileSharedEvent
import presentation.model.shared.OnReceivedTokenSharedEvent
import presentation.navigation.SharedEvent

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
        fetchProfile()
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

    fun fetchProfile() {
        reduce {
            copy(
                profile = profileRepository.fetchProfileLocal()
            )
        }
    }

    override fun onInitState(): State = State()

    override fun obtainSharedEvent(event: SharedEvent) {
        when (event) {
            is OnReceivedTokenSharedEvent, is OnQuitProfileSharedEvent -> {
                fetchProfile()
            }
        }
    }

}