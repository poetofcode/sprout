package presentation.navigation

import kotlinx.coroutines.flow.MutableSharedFlow

sealed interface Effect

data class NavigateEffect(
    val screen: BaseScreen<*>,
    val options: NavOptions = NavOptions.NONE,
    val tag: NavigatorTag = NavigatorTag.CURRENT,
) : Effect

data object NavigateBackEffect : Effect

data class SetBackHandlerEffect(
    val cb: () -> Boolean
) : Effect

object SharedMemory {
    
    val effectFlow: MutableSharedFlow<Effect> = MutableSharedFlow(
        extraBufferCapacity = 1
    )
    
} 