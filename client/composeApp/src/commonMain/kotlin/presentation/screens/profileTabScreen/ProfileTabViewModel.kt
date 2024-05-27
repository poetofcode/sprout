package presentation.screens.profileTabScreen

import data.repository.ProfileRepository
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.navigation.NavigateBackEffect
import presentation.navigation.NavigateEffect
import presentation.navigation.NavigatorTag
import presentation.navigation.SharedMemory
import presentation.screens.authScreen.AuthScreen

class ProfileTabViewModel(
    private val profileRepository: ProfileRepository
) : BaseViewModel() {

    fun onBackClick() = viewModelScope.launch {
        SharedMemory.effectFlow.emit(NavigateBackEffect)
    }

    fun onSignInToAccountButtonClick() {
        postEffect(
            NavigateEffect(
                AuthScreen("http://192.168.0.107:3000/front"),
                tag = NavigatorTag.TAB_ITSELF
            )
        )
    }

}