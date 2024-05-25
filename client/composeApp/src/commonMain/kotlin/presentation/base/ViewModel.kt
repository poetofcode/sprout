package presentation.base

import kotlinx.coroutines.CoroutineScope
import presentation.viewModelCoroutineScopeProvider

interface ViewModel {

    
}

abstract class BaseViewModel : ViewModel {

    protected val viewModelScope: CoroutineScope by lazy {
        viewModelCoroutineScopeProvider.scope
    }

}

class EmptyViewModel : BaseViewModel() {
    companion object {
        val INSTANCE = EmptyViewModel()
    }
}