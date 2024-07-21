package presentation.screens.startScreen

import data.repository.JokeRepository
import data.repository.ProfileRepository
import domain.model.JokeModel
import domain.model.Profile
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.base.postSharedEvent
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.model.Resource
import presentation.model.shared.OnQuitProfileSharedEvent
import presentation.model.shared.OnReceivedTokenSharedEvent
import presentation.model.shared.ShowDesktopNotificationSharedEvent
import presentation.navigation.NavigateEffect
import presentation.navigation.NavigatorTag
import presentation.navigation.SharedEvent
import presentation.screens.notificationsScreen.NotificationsScreen

class StartViewModel(
    val jokeRepository: JokeRepository,
    val profileRepository: ProfileRepository,
) : BaseViewModel<StartViewModel.State>() {

    companion object {
        const val REFRESH_TIMEOUT_SEC = 10
    }

    data class State(
        val jokes: List<JokeModel> = emptyList(),
        val readyState: Resource<Unit> = IdleResource,
        val profile: Profile? = null,
        val subscriptionState: Resource<Boolean> = IdleResource,
        val unseenNotificationCount: Int = 0
    )

    private var refreshJob: Job? = null

    init {
        onReload()
    }

    fun onReload() {
        fetchJokes()
        fetchProfile()
    }
    fun onScreenStart() {
        startRefreshTimer()
    }

    private fun startRefreshTimer() {
        if (refreshJob?.isActive == true) {
            return
        }
        refreshJob = viewModelScope.launch {
            while (refreshJob?.isActive == true) {
                delay(REFRESH_TIMEOUT_SEC * 1000L)
                onReload()
            }
        }
    }

    private fun fetchJokes() {
        viewModelScope.launch {
            try {
                reduce { copy(readyState = LoadingResource) }
                val jokes = jokeRepository.fetchJokes()
                reduce {
                    copy(
                        jokes = jokes,
                        readyState = CompleteResource(Unit)
                    )
                }
            } catch (e: Throwable) {
                state.value = state.value.copy(readyState = ExceptionResource(e))
                e.printStackTrace()
            }
        }
    }

    private fun fetchProfile() = reduce {
        copy(
            profile = profileRepository.fetchProfileLocal().apply {
                this?.let {
                    fetchSubscription()
                    fetchNotifications()
                }
            }
        )
    }

    fun fetchSubscription() = viewModelScope.launch {
        try {
            reduce { copy(subscriptionState = LoadingResource) }
            val isSubscribed = profileRepository.isSubscribed()
            reduce { copy(subscriptionState = CompleteResource(isSubscribed)) }
        } catch (e: Throwable) {
            e.printStackTrace()
            reduce { copy(subscriptionState = ExceptionResource(e)) }
        }
    }

    private fun fetchNotifications() {
        viewModelScope.launch {
            try {
                val notifications = profileRepository.fetchNotifications()
                val newNotificationCount = notifications.filterNot { it.seen }.size
                showDesktopNotification(newNotificationCount)
                reduce {
                    copy(
                        unseenNotificationCount = newNotificationCount,
                    )
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun showDesktopNotification(newNotificationCount: Int) {
        if (newNotificationCount > 0 && state.value.unseenNotificationCount != newNotificationCount) {
            postSharedEvent(
                ShowDesktopNotificationSharedEvent(
                    title = "Sprout",
                    message = "У вас $newNotificationCount новых сообщений"
                )
            )
        }
    }

    override fun onInitState(): State = State()

    override fun obtainSharedEvent(event: SharedEvent) {
        when (event) {
            is OnReceivedTokenSharedEvent, is OnQuitProfileSharedEvent -> {
                fetchProfile()
            }
        }
    }

    fun onSubscriptionChanged(enableSubscription: Boolean) = viewModelScope.launch {
        try {
            reduce { copy(subscriptionState = LoadingResource) }
            if (enableSubscription) {
                profileRepository.createSubscription()
            } else {
                profileRepository.deleteSubscription()
            }
            reduce { copy(subscriptionState = CompleteResource(enableSubscription)) }
        } catch (e: Throwable) {
            e.printStackTrace()
            reduce { copy(subscriptionState = ExceptionResource(e)) }
        }
    }

    fun navigateToNotificationsScreen() {
        postEffect(NavigateEffect(screen = NotificationsScreen(), tag = NavigatorTag.ROOT))
    }

    override fun onCleared() {
        refreshJob?.cancel()
    }

}