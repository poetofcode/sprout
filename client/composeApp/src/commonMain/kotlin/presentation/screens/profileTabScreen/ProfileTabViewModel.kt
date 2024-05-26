package presentation.screens.profileTabScreen

import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.navigation.NavigateBackEffect
import presentation.navigation.SharedMemory

class ProfileTabViewModel : BaseViewModel() {

    fun onBackClick() = viewModelScope.launch {
        SharedMemory.effectFlow.emit(NavigateBackEffect)
    }

}