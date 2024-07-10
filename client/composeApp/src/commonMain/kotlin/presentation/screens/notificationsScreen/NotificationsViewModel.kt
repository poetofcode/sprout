package presentation.screens.notificationsScreen

import data.repository.ProfileRepository
import domain.model.Notification
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.model.Resource
import presentation.navigation.NavigateBackEffect
import presentation.navigation.NavigatorTag

class NotificationsViewModel(
    val profileRepository: ProfileRepository,
) : BaseViewModel<NotificationsViewModel.State>() {

    data class State(
        val notifications: List<Notification> = emptyList(),
        val readyState: Resource<Unit> = IdleResource,
    )

    init {
        onReload()
    }

    fun onReload() {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        viewModelScope.launch {
            try {
                reduce { copy(readyState = LoadingResource) }
                val jokes = profileRepository.fetchNotifications()
                reduce {
                    copy(
                        notifications = jokes,
                        readyState = CompleteResource(Unit)
                    )
                }
            } catch (e: Throwable) {
                state.value = state.value.copy(readyState = ExceptionResource(e))
                e.printStackTrace()
            }
        }
    }

    override fun onInitState(): State = State()

    fun onBackClick() {
        postEffect(NavigateBackEffect(tag = NavigatorTag.ROOT))
    }

}

