package presentation.screens.profileScreen

import data.repository.ProfileRepository
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.navigation.NavigateEffect
import presentation.screens.authScreen.AuthScreen

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : BaseViewModel() {

    fun onSignInToAccountButtonClick() {
        postEffect(
            NavigateEffect(
                AuthScreen("http://192.168.0.108:3000/front"),
            )
        )
    }

}