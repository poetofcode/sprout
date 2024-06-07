package presentation.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

sealed interface Effect

data class NavigateEffect(
    val screen: BaseScreen<*>,
    val options: NavOptions = NavOptions.NONE,
    val tag: NavigatorTag = NavigatorTag.CURRENT,
) : Effect

data object NavigateBackEffect : Effect

data class ShowSnackErrorEffect(
    val text: String,
) : Effect

data class SetBackHandlerEffect(
    val cb: () -> Boolean
) : Effect

interface SharedEvent

object SharedMemory {
    
    val effectFlow: MutableSharedFlow<Effect> = MutableSharedFlow(
        extraBufferCapacity = 1
    )

    // Используется для обмена событиями между разными ViewModel'ями
    // Работа с ним происходит в базовом классе BaseViewModel
    val eventFlow = MutableSharedFlow<SharedEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

} 