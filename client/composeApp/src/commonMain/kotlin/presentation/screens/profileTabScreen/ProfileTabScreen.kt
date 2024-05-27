package presentation.screens.profileTabScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.dataToJsonString
import com.multiplatform.webview.jsbridge.processParams
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import kotlinx.serialization.Serializable
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

        val state = rememberWebViewState(url = frontUrl)
        LaunchedEffect(Unit) {
            state.webSettings.apply {
                isJavaScriptEnabled = true
                zoomLevel = 1.2

                // Mobile
                customUserAgentString =
                    "Mozilla/5.0 (Linux; Android 14; SM-N960U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.6312.80 Mobile Safari/537.36"

                // PC
                // customUserAgentString = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1) AppleWebKit/625.20 (KHTML, like Gecko) Version/14.3.43 Safari/625.20"

                backgroundColor = Color.White

                androidWebSettings.apply {
                    isAlgorithmicDarkeningAllowed = true
                    safeBrowsingEnabled = true
                    supportZoom = true
                    domStorageEnabled = true
                }

                desktopWebSettings.apply {
                    offScreenRendering = false
                    // transparent = false
                }
            }
        }

        val jsBridge = rememberWebViewJsBridge()
        LaunchedEffect(Unit) {
            jsBridge.register(GreetJsMessageHandler())
        }


        LaunchedEffect(state.loadingState) {
            if (state.loadingState is LoadingState.Finished) {
//                state.cookieManager.setCookie(
//                    "https://github.com",
//                    Cookie(
//                        name = "test",
//                        value = "value",
//                        domain = "github.com",
//                        expiresDate = 1896863778
//                    )
//                )
//                Logger.i {
//                    "cookie: ${state.cookieManager.getCookies("https://github.com")}"
//                }
//                state.cookieManager.removeAllCookies()

                println(
                    "cookie: ${state.cookieManager.getCookies(frontUrl)}"
                )
            }
        }

        var textFieldValue by remember(state.lastLoadedUrl) {
            mutableStateOf(state.lastLoadedUrl)
        }
        MaterialTheme {
            Column {
                TopAppBar(
                    title = { Text(text = state.pageTitle ?: state.lastLoadedUrl ?: "Загрузка..") },
                    navigationIcon = {
                        IconButton(onClick = { onBackClick() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                )

                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        if (state.errorsForCurrentRequest.isNotEmpty()) {
                            Image(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Error",
                                colorFilter = ColorFilter.tint(Color.Red),
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(8.dp),
                            )
                        }

                        OutlinedTextField(
                            value = textFieldValue ?: "",
                            onValueChange = { textFieldValue = it },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                        )
                    }

                    Button(
                        onClick = {
                            textFieldValue?.let {
                                navigator.loadUrl(it)
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterVertically),
                    ) {
                        Text("Go")
                    }
                }

                val loadingState = state.loadingState
                if (loadingState is LoadingState.Loading) {
                    LinearProgressIndicator(
                        progress = loadingState.progress,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                WebView(
                    state = state,
                    modifier = Modifier.fillMaxSize(),
                    navigator = navigator,
                    webViewJsBridge = jsBridge,
                )
            }
        }
    }

}



@Serializable
data class GreetModel(
    val message: String,
)

class GreetJsMessageHandler : IJsMessageHandler {

    override fun methodName(): String {
        return "Greet"
    }

    override fun handle(
        message: JsMessage,
        navigator: WebViewNavigator?,
        callback: (String) -> Unit
    ) {
        // println("Greet Handler Get Message: $message")
        val param = processParams<GreetModel>(message)
        val data = GreetModel("KMM Received ${/*param.message*/"Любое сообщение"}")
        callback(dataToJsonString(data))
        // callback("{ response: \"Hello\" }")
    }
}