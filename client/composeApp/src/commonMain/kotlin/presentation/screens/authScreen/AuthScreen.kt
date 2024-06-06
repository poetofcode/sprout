package presentation.screens.authScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.Tabs
import presentation.navigation.BaseScreen
import specific.BackHandler

class AuthScreen : BaseScreen<AuthViewModel>() {
    override val screenId: String
        get() = Tabs.PROFILE.key

    override val viewModel: AuthViewModel
        get() = viewModelStore.getViewModel<AuthViewModel>()

    override val isMenuVisible: Boolean = false

    val state get() = viewModel.state.value

    @Composable
    override fun Content() {
        BackHandler { viewModel.onBackClick(); true }

        MaterialTheme {
            Column {
                TopAppBar(
                    title = { Text(text = "Вход в аккаунт") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onBackClick() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .wrapContentSize()
                            .widthIn(0.dp, 300.dp)
                            .align(Alignment.Center)
                    ) {
                        OutlinedTextField(
                            value = viewModel.state.value.email,
                            onValueChange = {
                                viewModel.onEmailChanged(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("E-mail") },
                            maxLines = 1,
                        )

                        OutlinedTextField(
                            value = viewModel.state.value.password,
                            onValueChange = {
                                viewModel.onPasswordChanged(it)
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            label = { Text("Пароль") },
                            maxLines = 1,
                        )

                        Button(
                            onClick = {
                                viewModel.onSubmitClick()
                            },
                            modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally),
                        ) {
                            Text("Войти")
                        }

                    }

                }
            }
        }
    }

}