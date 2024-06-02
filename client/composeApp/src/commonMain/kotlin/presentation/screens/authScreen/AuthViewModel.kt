package presentation.screens.authScreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import data.repository.ProfileRepository
import domain.model.Profile
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.navigation.NavigateBackEffect


class AuthViewModel(
    private val profileRepository: ProfileRepository,
) : BaseViewModel() {

    data class State(
        val profile: Profile? = null
    )

    val state = mutableStateOf(State())

    fun onBackClick() = viewModelScope.launch {
        // SharedMemory.effectFlow.emit(NavigateBackEffect)
        postEffect(NavigateBackEffect)
    }

    fun fetchProfile() {
        profileRepository.fetchProfileLocal()
    }

}

