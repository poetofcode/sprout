package presentation.screens.profileTabScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.web.rememberWebViewNavigator
import presentation.Tabs
import presentation.navigation.BaseScreen
import specific.BackHandler

class ProfileTabScreen : BaseScreen<ProfileTabViewModel>() {
    override val screenId: String
        get() = Tabs.PROFILE.key

    override val viewModel: ProfileTabViewModel
        get() = viewModelStore.getViewModel<ProfileTabViewModel>()

    override val isMenuVisible: Boolean = true

    @Composable
    override fun Content() {
        val navigator = rememberWebViewNavigator()

        fun onBackClick(): Boolean {
            if (navigator.canGoBack) {
                navigator.navigateBack()
            } else {
                viewModel.onBackClick()
            }
            return true
        }

        BackHandler { onBackClick() }

        val isAuth = false

        MaterialTheme {
            Column {
                TopAppBar(
                    title = { Text(text = "Профиль") },
                    navigationIcon = {},
                    actions = {
//                        if (readyState !is LoadingResource) {
//                            IconButton(onClick = {
//                                viewModel.fetchFeed()
//                            }) {
//                                Icon(
//                                    imageVector = Icons.Default.Refresh,
//                                    contentDescription = "Reload",
//                                )
//                            }
//                        }
                    }
                )

                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    if (!isAuth) {
                        UnsignedProfile()
                    } else {
                        SignedProfile()
                    }
                }

            }
        }
    }

    @Composable
    fun UnsignedProfile() {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.wrapContentSize().align(Alignment.Center)) {
                Text(
                    text = "Войдите в аккаунт, чтобы получить доступ ко всем функциям приложения",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 30.dp),
                    onClick = {
                        viewModel.onSignInToAccountButtonClick()
                    }
                ) {
                    Text(text = "Войти в аккаунт")
                }
            }
        }
    }

    @Composable
    fun SignedProfile() {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.wrapContentSize().align(Alignment.Center)) {
                Text(
                    text = "Вы уже авторизованы",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }

}