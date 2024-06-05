package presentation.screens.authScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.web.rememberWebViewNavigator
import presentation.Tabs
import presentation.navigation.BaseScreen
import specific.BackHandler

class AuthScreen: BaseScreen<AuthViewModel>() {
    override val screenId: String
        get() = Tabs.PROFILE.key

    override val viewModel: AuthViewModel
        get() = viewModelStore.getViewModel<AuthViewModel>()

    override val isMenuVisible: Boolean = false

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
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                )

                Row {
//                    Box(modifier = Modifier.weight(1f)) {
//                        OutlinedTextField(
//                            value = textFieldValue ?: "",
//                            onValueChange = { textFieldValue = it },
//                            modifier = Modifier.fillMaxWidth(),
//                            maxLines = 1,
//                        )
//                    }
//
//                    Button(
//                        onClick = {
//                            textFieldValue?.let {
//                                // navigator.loadUrl(it)
//                            }
//                        },
//                        modifier = Modifier.align(Alignment.CenterVertically),
//                    ) {
//                        Text("Go")
//                    }
                }
            }
        }
    }

}