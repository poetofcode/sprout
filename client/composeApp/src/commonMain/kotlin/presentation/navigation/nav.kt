package presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import presentation.LocalMainAppState
import presentation.base.ViewModel
import presentation.base.ViewModelStore
import specific.BackHandler


typealias AnyScreen = BaseScreen<*>

interface Screen<T : ViewModel> {
    val screenId: String

    val viewModel: T

    @Composable
    fun Content()
}

abstract class BaseScreen<T : ViewModel> : Screen<T> {

    protected lateinit var viewModelStore: ViewModelStore

    override val screenId: String
        get() = this::class.java.typeName

    protected open val isMenuVisible: Boolean = false

    @Composable
    protected fun setMainMenuVisibility() {
        val appState = LocalMainAppState.current
        appState.isMenuVisible.value = isMenuVisible
    }

    fun setVMStore(viewModelStore: ViewModelStore) {
        this.viewModelStore = viewModelStore
    }

    @Composable
    fun PrepareContent() {
        val scope = rememberCoroutineScope()
        scope.launch {
            SharedMemory.effectFlow.emit(SetBackHandlerEffect { false })
        }
        setMainMenuVisibility()
        Content()
    }


}

interface NavState {
    val screens: State<List<AnyScreen>>

    fun push(screen: AnyScreen)

    fun pop()

    fun moveToFront(screenId: String)
}

class NavStateImpl(val viewModelStore: ViewModelStore) : NavState {
    private val _screens = mutableStateOf<List<AnyScreen>>(emptyList())

    override val screens: State<List<AnyScreen>>
        get() = _screens

    override fun push(screen: AnyScreen) {
        screen.setVMStore(viewModelStore)
        _screens.value += screen
    }

    override fun pop() {
        if (_screens.value.size <= 1) {
            return
        }
        _screens.value.lastOrNull()?.let { topScreen ->
            viewModelStore.removeViewModel(topScreen.viewModel)
        }
        _screens.value = _screens.value.subList(0, _screens.value.size - 1)
    }

    override fun moveToFront(screenId: String) {
        var currScreens = _screens.value.toMutableList()
        currScreens.firstOrNull {
            it.screenId == screenId
        }?.let { found ->
            currScreens = currScreens.filterNot { it.screenId == screenId }.toMutableList()
            currScreens.add(found)
            _screens.value = currScreens
        }
    }

}


@Composable
fun Navigator(
    modifier: Modifier = Modifier,
    state: NavState,
) {
    Box(modifier) {
        val screens = state.screens.value
        screens.lastOrNull()?.PrepareContent()
    }
}