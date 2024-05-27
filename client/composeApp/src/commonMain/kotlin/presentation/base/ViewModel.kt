package presentation.base

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import presentation.navigation.Effect
import presentation.navigation.LocalNavigators
import presentation.viewModelCoroutineScopeProvider

interface ViewModel {

    
}

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

        println("BaseViewModel onEffect: $effect ($this)")
        println("Navigators:")
        println(navigators)

    }.launchIn(viewModelScope)

}

fun BaseViewModel.postEffect(effect: Effect) {
    viewModelScope.launch {
        effectFlow.emit(effect)
    }
}
