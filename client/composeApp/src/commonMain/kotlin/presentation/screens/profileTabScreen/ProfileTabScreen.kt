package presentation.screens.profileTabScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.rememberWebViewNavigator
import presentation.Tabs
import presentation.navigation.BaseScreen
import specific.BackHandler

class ProfileTabScreen(
    val frontUrl: String,
) : BaseScreen<ProfileTabViewModel>() {
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
                    // TODO
                }

            }
        }
    }

}