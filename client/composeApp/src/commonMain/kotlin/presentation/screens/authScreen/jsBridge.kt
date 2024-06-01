package presentation.screens.authScreen

import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.web.WebViewNavigator
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class JsTokenInfo(
    val token: String,
    val email: String,
)

private val json by lazy {
    Json {
        ignoreUnknownKeys = true
    }
}

class JsTokenHandler(
    private val onTokenReceived: (JsTokenInfo) -> Unit
) : IJsMessageHandler {

    override fun methodName(): String {
        return "saveToken"
    }

    override fun handle(
        message: JsMessage,
        navigator: WebViewNavigator?,
        callback: (String) -> Unit
    ) {
        // TODO catch errors
        val decoded = json.decodeFromString<JsTokenInfo>(message.params)

        callback("{ message: \"Hello\" }")
        onTokenReceived(decoded)
    }
}
