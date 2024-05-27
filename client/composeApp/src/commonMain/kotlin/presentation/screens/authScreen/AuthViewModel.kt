package presentation.screens.authScreen

import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.navigation.NavigateBackEffect
import presentation.navigation.SharedMemory

class AuthViewModel : BaseViewModel() {

    fun onBackClick() = viewModelScope.launch {
        SharedMemory.effectFlow.emit(NavigateBackEffect)
    }

}

