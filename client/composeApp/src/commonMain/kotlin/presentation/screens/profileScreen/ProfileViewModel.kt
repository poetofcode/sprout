package presentation.screens.profileScreen

import androidx.compose.runtime.mutableStateOf
import domain.model.Profile
import data.repository.ProfileRepository
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.model.shared.OnReceivedTokenSharedEvent
import presentation.navigation.NavigateEffect
import presentation.navigation.SharedEvent
import presentation.screens.authScreen.AuthScreen

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : BaseViewModel<ProfileViewModel.State>() {

    data class State(
        val profile: Profile? = null
    )

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        profileRepository.fetchProfileLocal()?.let { profile ->
            reduce { copy(profile = profile) }
        }
    }

    fun onSignInToAccountButtonClick() {
        postEffect(
            NavigateEffect(
                AuthScreen(),
            )
        )
    }

    override fun onInitState() : State = State()

    override fun obtainSharedEvent(event: SharedEvent) {
        when (event) {
            is OnReceivedTokenSharedEvent -> {
                profileRepository.saveProfileLocal(
                    Profile(
                        token = event.token,
                        email = event.email,
                    )
                )
                fetchProfile()
            }
        }
    }

}