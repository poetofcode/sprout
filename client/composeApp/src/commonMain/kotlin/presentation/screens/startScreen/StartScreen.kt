package presentation.screens.startScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.model.JokeModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.navigation.BaseScreen
import specific.ScrollBar
import specific.ScrollBarOrientation
import specific.ScrollableComponentState

@OptIn(ExperimentalResourceApi::class)
class StartScreen : BaseScreen<StartViewModel>() {

    override val viewModel: StartViewModel
        get() = viewModelStore.getViewModel<StartViewModel>(screenId)

    override val isMenuVisible: Boolean = true

    private val listState = LazyListState()

    val state get() = viewModel.state.value

    @Composable
    override fun Content() = with(viewModel.state.value) {
        MaterialTheme {
            Column {
                TopAppBar(
                    title = { Text(text = "Главная") },
                    navigationIcon = {},
                    actions = {
                        if (readyState !is LoadingResource) {
                            IconButton(onClick = {
                                viewModel.fetchJokes()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reload",
                                )
                            }
                        }
                    }
                )

                Box(Modifier.fillMaxSize().weight(1f)) {
                    MainContent(Modifier.padding(bottom = 50.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(Color.Cyan)
                            .align(BottomCenter)
                    ) {
                        Switch(
                            modifier = Modifier.align(Alignment.CenterEnd).padding(horizontal = 20.dp),
                            checked = true,
                            onCheckedChange = { }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun MainContent(modifier: Modifier) {
        Box(modifier = modifier.fillMaxWidth()) {
            when (val readyState = state.readyState) {
                is CompleteResource -> Jokes(state.jokes)

                is ExceptionResource -> {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Ошибка загрузки", color = Color.Red)
                        Spacer(Modifier.size(10.dp))
                        Text(text = "${readyState.exception}")
                    }
                }

                is IdleResource -> {
                    Box(Modifier.fillMaxSize()) {
                        Text(
                            text = "Список пуст",
                            color = Color.Gray,
                            modifier = Modifier.align(
                                Center
                            )
                        )
                    }
                }

                else -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }

    @Composable
    private fun Jokes(jokes: List<JokeModel>) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxHeight().padding().weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(jokes) { joke ->
                    Joke(joke = joke)
                }
            }

            ScrollBar(
                modifier = Modifier.width(20.dp).fillMaxHeight(),
                orientation = ScrollBarOrientation.VERTICAL,
                state = ScrollableComponentState.LazyListComponentState(listState)
            )
        }
    }

    @Composable
    private fun Joke(joke: JokeModel) {
        // val context = LocalContext.current
        Column(
            modifier = Modifier
                .clickable {
                    //>>>>>>>>
                    // Do nothing
                }
                .padding(vertical = 5.dp)
                .fillMaxWidth()
                .background(
                    color = Color.LightGray, // MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(8.dp)
        ) {
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = joke.text.orEmpty(), fontSize = 16.sp)
        }
    }

}
