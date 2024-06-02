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
) : BaseViewModel() {

    data class State(
        val profile: Profile? = null
    )

    val state = mutableStateOf(State())

    init {
        fetchProfile()
    }

    private fun reduce(cb: State.() -> State) {
        state.value = cb(state.value)
    }


    private fun fetchProfile() {
        profileRepository.fetchProfileLocal()?.let { profile ->
            reduce { copy(profile = profile) }
        }
    }

    fun onSignInToAccountButtonClick() {
        postEffect(
            NavigateEffect(
                AuthScreen("http://192.168.0.108:3000/front"),
            )
        )
    }

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