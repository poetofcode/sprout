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
        val subscriptionState: Resource<Boolean> = IdleResource,
    )

    init {
        onReload()
    }

    fun onReload() {
        fetchJokes()
        fetchProfile()
    }

    private fun fetchJokes() {
        viewModelScope.launch {
            try {
                reduce { copy(readyState = LoadingResource) }
                val jokes = jokeRepository.fetchJokes()
                reduce {
                    copy(
                        jokes = jokes,
                        readyState = CompleteResource(Unit)
                    )
                }
            } catch (e: Throwable) {
                state.value = state.value.copy(readyState = ExceptionResource(e))
                e.printStackTrace()
            }
        }
    }

    private fun fetchProfile() = reduce {
        copy(
            profile = profileRepository.fetchProfileLocal().apply {
                this?.let {
                    fetchSubscription()
                }
            }
        )
    }

    fun fetchSubscription() = viewModelScope.launch {
        try {
            reduce { copy(subscriptionState = LoadingResource) }
            val isSubscribed = profileRepository.isSubscribed()
            reduce { copy(subscriptionState = CompleteResource(isSubscribed)) }
        } catch (e: Throwable) {
            e.printStackTrace()
            reduce { copy(subscriptionState = ExceptionResource(e)) }
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

    fun onSubscriptionChanged(enableSubscription: Boolean) = viewModelScope.launch {
        try {
            reduce { copy(subscriptionState = LoadingResource) }
            if (enableSubscription) {
                profileRepository.createSubscription()
            } else {
                profileRepository.deleteSubscription()
            }
            reduce { copy(subscriptionState = CompleteResource(enableSubscription)) }
        } catch (e: Throwable) {
            e.printStackTrace()
            reduce { copy(subscriptionState = ExceptionResource(e)) }
        }
    }

}