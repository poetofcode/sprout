package presentation.base

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import presentation.navigation.Effect
import presentation.navigation.LocalNavigators
import presentation.navigation.NavOptions
import presentation.navigation.NavigateBackEffect
import presentation.navigation.NavigateEffect
import presentation.navigation.NavigatorInfo
import presentation.navigation.NavigatorTag
import presentation.navigation.SetBackHandlerEffect
import presentation.viewModelCoroutineScopeProvider

interface ViewModel

abstract class BaseViewModel : ViewModel {

    val effectFlow: MutableSharedFlow<Effect> = MutableSharedFlow(
        extraBufferCapacity = 1
    )

    val viewModelScope: CoroutineScope by lazy {
        viewModelCoroutineScopeProvider.scope
    }

}

class EmptyViewModel : BaseViewModel() {
    companion object {
        val INSTANCE = EmptyViewModel()
    }
}

@Composable
fun BaseViewModel.collectEffects() {
    val navigators = LocalNavigators.current

    effectFlow.onEach { effect ->

        when (effect) {
            NavigateBackEffect -> {
                val navigatorInfo = findNavigatorInfoByTag(navigators, NavigatorTag.CURRENT)
                navigatorInfo.navState.pop()
            }

            is NavigateEffect -> {
                val navigatorInfo = findNavigatorInfoByTag(navigators, effect.tag)
                when (effect.options) {
                    NavOptions.NONE -> navigatorInfo.navState.push(effect.screen)
                    NavOptions.REPLACE -> navigatorInfo.navState.moveToFront(effect.screen.screenId)
                    NavOptions.REPLACE_ALL -> {
                        // TODO
                        // navigatorInfo.navState.clearScreens()
                        // navigatorInfo.navState.push(effect.screen)
                    }
                }
            }

            is SetBackHandlerEffect -> {

            }
        }

    }.launchIn(viewModelScope)

}

fun BaseViewModel.postEffect(effect: Effect) {
    viewModelScope.launch {
        effectFlow.emit(effect)
    }
}

fun findNavigatorInfoByTag(navigators: List<NavigatorInfo>, tag: NavigatorTag) : NavigatorInfo {
    val navigatorInfo = if (tag != NavigatorTag.CURRENT) {
        val navigatorType = navigators.firstOrNull { it.tag == tag }
        checkNotNull(navigatorType) { logErrorNotFound(tag.name) }
        navigatorType
    } else {
        val lastNavigatorType = navigators.lastOrNull()
        checkNotNull(lastNavigatorType) { logErrorNotFound(tag.name) }
        lastNavigatorType
    }
    return navigatorInfo
}

fun logErrorNotFound(arg: String) {
    println("Navigator with tag '${arg}' in not found'}")
}
