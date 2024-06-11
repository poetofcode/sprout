package presentation.screens.profileScreen

import data.repository.ProfileRepository
import domain.model.Profile
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.base.postSharedEvent
import presentation.base.postSideEffect
import presentation.model.shared.OnQuitProfileSharedEvent
import presentation.model.shared.OnReceivedTokenSharedEvent
import presentation.navigation.HideBottomSheetEffect
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
        val profile = profileRepository.fetchProfileLocal()
        reduce { copy(profile = profile) }
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

            OnQuitProfileSharedEvent -> {
                fetchProfile()
                postSideEffect(HideBottomSheetEffect)
            }
        }
    }

    fun onConfirmQuit() {
        viewModelScope.launch {
            try {
                profileRepository.deleteSession()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            joinAll()
            postSharedEvent(OnQuitProfileSharedEvent)
        }
    }


}