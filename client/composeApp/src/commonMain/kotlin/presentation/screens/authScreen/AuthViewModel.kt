package presentation.screens.authScreen

import data.repository.ProfileRepository
import domain.model.Profile
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.navigation.NavigateBackEffect


class AuthViewModel(
    private val profileRepository: ProfileRepository,
) : BaseViewModel<AuthViewModel.State>() {

    init {
        fetchProfile()
    }

    data class State(
        val profile: Profile? = null
    )

    fun onBackClick() = viewModelScope.launch {
        postEffect(NavigateBackEffect)
    }

    fun fetchProfile() {
        val profile = profileRepository.fetchProfileLocal()
        profile?.let {
            reduce { copy(
                profile = it
            ) }
        }
    }

    override fun onInitState(): State = State()

}

