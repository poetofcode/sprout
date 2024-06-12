package presentation.screens.startScreen

import androidx.compose.foundation.Image
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.model.JokeModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.navigation.BaseScreen
import specific.AsyncImage
import specific.ScrollBar
import specific.ScrollBarOrientation
import specific.ScrollableComponentState
import sproutclient.composeapp.generated.resources.Res
import sproutclient.composeapp.generated.resources.ic_cell_fav_disabled
import sproutclient.composeapp.generated.resources.ic_cell_fav_enabled

@OptIn(ExperimentalResourceApi::class)
class StartScreen : BaseScreen<StartViewModel>() {

    override val viewModel: StartViewModel
        get() = viewModelStore.getViewModel<StartViewModel>(screenId)

    override val isMenuVisible: Boolean = true

    private val listState = LazyListState()


    @Composable
    override fun Content() = with(viewModel.state.value) {


        MaterialTheme {
            Column {
                TopAppBar(
                    title = { Text(text = "Главная") },
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
                    when (readyState) {
                        is CompleteResource -> Posts(posts)

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
        }
    }

    @Composable
    private fun Posts(posts: List<JokeModel>) {
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
                items(posts) { post ->
                    Post(post = post)
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
    private fun Post(post: JokeModel) {
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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Center
            ) {
                post.image?.let { imageUrl ->
                    AsyncImage(
                        modifier = Modifier.height(250.dp),
                        url = imageUrl,
                        loadingView = {},
                        errorView = {}
                    )
                }

                val favIcon = if (post.isFavorite) {
                    Res.drawable.ic_cell_fav_enabled
                } else {
                    Res.drawable.ic_cell_fav_disabled
                }

                Image(
                    modifier = Modifier.align(alignment = TopEnd)
                        .clickable {
                            viewModel.updatePostFavorite(post.link, !post.isFavorite)
                        },
                    painter = painterResource(favIcon),
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.size(8.dp))
            Text(text = post.text.orEmpty(), fontSize = 16.sp)
        }
    }

}
