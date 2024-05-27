package presentation.screens.profileTabScreen

import data.repository.ProfileRepository
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.navigation.NavigateBackEffect
import presentation.navigation.SharedMemory

class ProfileTabViewModel(
    private val profileRepository: ProfileRepository
) : BaseViewModel() {

    fun onBackClick() = viewModelScope.launch {
        SharedMemory.effectFlow.emit(NavigateBackEffect)
    }

}