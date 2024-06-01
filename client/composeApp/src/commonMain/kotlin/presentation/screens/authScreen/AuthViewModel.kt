package presentation.screens.authScreen

import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.navigation.NavigateBackEffect

class AuthViewModel : BaseViewModel() {

    fun onBackClick() = viewModelScope.launch {
        // SharedMemory.effectFlow.emit(NavigateBackEffect)
        postEffect(NavigateBackEffect)
    }

}

