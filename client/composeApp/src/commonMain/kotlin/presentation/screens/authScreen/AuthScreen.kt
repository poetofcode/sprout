package presentation.screens.authScreen

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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.cookie.Cookie
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import io.ktor.http.headers
import kotlinx.coroutines.flow.filter
import presentation.Tabs
import presentation.base.postSharedEvent
import presentation.model.shared.OnReceivedTokenSharedEvent
import presentation.navigation.BaseScreen
import specific.BackHandler
import java.util.TimeZone

/*
    "http://192.168.0.107:3000/front"
 */

class AuthScreen(
    private val frontUrl: String,
    private val useToken: Boolean = false,
) : BaseScreen<AuthViewModel>() {
    override val screenId: String
        get() = Tabs.PROFILE.key

    override val viewModel: AuthViewModel
        get() = viewModelStore.getViewModel<AuthViewModel>()

    override val isMenuVisible: Boolean = false

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

        val state = rememberWebViewState(
            url = frontUrl,
            additionalHttpHeaders = mapOf(
                "Authorization" to "12345"
            )
        )

        var cookieBeforeSize = remember { mutableStateOf(0) }
        var cookieAfterAddedSize = remember { mutableStateOf(0) }
        cookieSample("192.168.0.108", state, cookieBeforeSize, cookieAfterAddedSize)

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
            jsBridge.register(JsSaveTokenHandler { tokenInfo ->
                viewModel.postSharedEvent(
                    OnReceivedTokenSharedEvent(
                        tokenInfo.token,
                        tokenInfo.email,
                    )
                )
                viewModel.onBackClick()
            })
            viewModel.state.value.profile?.token?.let { token ->
                jsBridge.register(JsGetTokenHandler(token))
            }
        }

        /*
        LaunchedEffect(state.loadingState) {
            if (state.loadingState is LoadingState.Finished) {
                state.cookieManager.setCookie(
                    frontUrl,
                    Cookie(
                        name = "token",
                        value = ,
                        domain = frontUrl,
                        expiresDate = 1896863778
                    )
                )
//                Logger.i {
//                    "cookie: ${state.cookieManager.getCookies("https://github.com")}"
//                }
                state.cookieManager.removeAllCookies()

                println(
                    "cookie: ${state.cookieManager.getCookies(frontUrl)}"
                )
            }
        }

         */

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
                                // navigator.loadUrl(it)
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

@Composable
internal fun cookieSample(
    domain: String,
    state: WebViewState,
    cookieBefore: MutableState<Int>,
    cookieAfter: MutableState<Int>
) {
    LaunchedEffect(state) {
        snapshotFlow { state.loadingState }
            .filter { it is LoadingState.Finished }
            .collect {
                // val tokenManagerImpl = TokenManagerImpl()
                // val domain = "https://abc.com"
                // val shortDomain = "abc.com"
//                val parts =
//                    tokenManagerImpl.cookieLoggedIn?.split("=") // Sépare la chaîne sur le signe égal

                // val systemTimeZone = TimeZone.currentSystemDefault()
                // Obtenir la date actuelle dans la zone horaire du système
                // val today: LocalDate = Clock.System.todayIn(systemTimeZone)
                // val localDate = today.plus(DatePeriod(days = 30))
                //val localDateTime =
                //    LocalDateTime(localDate.year, localDate.monthNumber, localDate.dayOfMonth, 0, 0)
                // Convertir LocalDateTime en Instant
                // val instant = localDateTime.toInstant(TimeZone.UTC)
                println(
                    "cookie before is: ${state.cookieManager.getCookies(domain).size}"
                )
                cookieBefore.value = state.cookieManager.getCookies(domain).size
//                parts?.let {
//                    if (parts.size >= 2) { // Assurez-vous qu'il y a au moins 2 parties
//                        val name = parts[0] // La partie avant le signe égal
//                        val value = parts[1] // La partie après le signe égal

                state.cookieManager.setCookie(
                    domain,
                    Cookie(
                        name = "token",
                        value = "12345",
//                        domain = domain,
                        path = "/"
                    ),
                )

//                    }


            }

        state.cookieManager.setCookie(
            domain,
            Cookie(
                name = "token",
                value = "12345",
//                domain = domain,
                path = "/"
            ),
        )

        println(
            "cookie add size: ${state.cookieManager.getCookies(domain).size}"
        )
        cookieAfter.value = state.cookieManager.getCookies(domain).size

    }
}
