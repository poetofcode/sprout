package presentation.screens.profileScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import presentation.Tabs
import presentation.navigation.BaseScreen


class ProfileScreen : BaseScreen<ProfileViewModel>() {
    override val screenId: String
        get() = Tabs.PROFILE.key

    override val viewModel: ProfileViewModel
        get() = viewModelStore.getViewModel<ProfileViewModel>()

    override val isMenuVisible: Boolean = true

    val state get() = viewModel.state.value

    @Composable
    override fun Content() {
        val isAuth = state.profile != null

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
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(vertical = 30.dp),
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
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(
                        text = "Вы авторизованы",
                        color = Color.Gray,
                    )
                    Text(
                        text = state.profile?.email.orEmpty(),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 6.dp),
                    )
                }

                ClickableItem(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = "Выйти из аккаунта"
                ) {

                }
            }
        }
    }

    @Composable
    fun ClickableItem(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
        Box(modifier.clickable {
            onClick()
        }.fillMaxWidth().padding(10.dp)) {
            Text(
                text = text,
                color = Color.Black,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }

}
