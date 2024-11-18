package presentation.screens.authScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.Tabs
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.navigation.BaseScreen
import presentation.theme.AppTheme
import specific.BackHandler

@OptIn(ExperimentalMaterial3Api::class)
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

        AppTheme {

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


                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    when (val readyState = state.readyState) {
                        is CompleteResource, is ExceptionResource, IdleResource-> {
                            SigninForm()

                            if (readyState is ExceptionResource) {
                                LaunchedEffect(readyState) {
                                    viewModel.onSubmitError(readyState.exception)
                                }
                            }
                        }

                        LoadingResource -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SigninForm() {
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