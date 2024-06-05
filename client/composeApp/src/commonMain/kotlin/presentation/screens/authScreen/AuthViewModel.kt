package presentation.screens.authScreen

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import data.repository.ProfileRepository
import domain.model.Profile
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.base.postSharedEvent
import presentation.model.shared.OnReceivedTokenSharedEvent
import presentation.navigation.NavigateBackEffect


class AuthViewModel(
    private val profileRepository: ProfileRepository,
) : BaseViewModel<AuthViewModel.State>() {

    init {
        fetchProfile()
    }

    data class State(
        val profile: Profile? = null,
        val email: TextFieldValue = TextFieldValue(),
        val password: TextFieldValue = TextFieldValue(),
    )

    fun onBackClick() = viewModelScope.launch {
        postEffect(NavigateBackEffect)
    }

    fun fetchProfile() {
        val profile = profileRepository.fetchProfileLocal()
        profile?.let {
            reduce { copy(
                profile = it
            ) }
        }
    }

    override fun onInitState(): State = State()

    fun onEmailChanged(value: TextFieldValue) {
        reduce { copy(email = value) }
    }

    fun onPasswordChanged(value: TextFieldValue) {
        reduce { copy(password = value) }
    }

    fun onSubmitClick() {
        viewModelScope.launch {
            try {
                val profile = profileRepository.createSession(
                    email = state.value.email.text,
                    password = state.value.password.text,
                )
                postSharedEvent(OnReceivedTokenSharedEvent(profile.token, profile.email))
                onBackClick()
            } catch (e: Throwable) {
                // state.value = state.value.copy(readyState = ExceptionResource(e))
                e.printStackTrace()
            }
        }
    }

}

