package presentation.screens.bookmarkTabScreen

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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.model.PostModel
import presentation.Tabs
import presentation.navigation.BaseScreen
import presentation.navigation.NavigateEffect
import presentation.navigation.SharedMemory
import presentation.screens.postDetailsScreen.PostDetailsScreen
import specific.AsyncImage
import specific.ScrollBar
import specific.ScrollBarOrientation
import specific.ScrollableComponentState


class BookmarkTabScreen : BaseScreen<BookmarkTabViewModel>() {
    override val screenId: String
        get() = Tabs.BOOKMARK.key

    override val viewModel: BookmarkTabViewModel
        get() = viewModelStore.getViewModel<BookmarkTabViewModel>(screenId)

    override val isMenuVisible: Boolean = true
    private val listState = LazyListState()

    @Composable
    override fun Content() = with(viewModel.state.value) {
        LaunchedEffect(key1 = null) {
            viewModel.fetchBookmarks()
        }

//        val coroutineScope = rememberCoroutineScope()
//        LaunchedEffect(key1 = Unit) {
//            coroutineScope.launch() {
//                viewModel.fetchBookmarks()
//            }
//        }

        MaterialTheme {
            Column {
                TopAppBar(
                    title = { Text(text = "Лента") },
                    navigationIcon = {},
//                    actions = {
//                        if (readyState !is LoadingResource) {
//                            IconButton(onClick = {
//                                viewModel.fetchBookmarks()
//                            }) {
//                                Icon(
//                                    imageVector = androidx.compose.material.icons.Icons.Default.Refresh,
//                                    contentDescription = "Reload",
//                                )
//                            }
//                        }
//                    }
                )

                Box(modifier = androidx.compose.ui.Modifier.fillMaxWidth().weight(1f)) {
                    if (posts.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().align(Alignment.Center),
                            contentAlignment = Alignment.Center) {
                            Text(modifier = Modifier,
                                text = "У вас еще нет избранного",
                                color = Color.Blue)
                        }
                    }
                    else {
                        Posts(posts)
                    }

//                    when (readyState) {
//                        is CompleteResource -> Posts(posts)
//
//                        is ExceptionResource -> {
//                            Column(
//                                androidx.compose.ui.Modifier.fillMaxSize(),
//                                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
//                                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
//                            ) {
//                                Text(text = "Ошибка загрузки", color = androidx.compose.ui.graphics.Color.Red)
//                                Spacer(androidx.compose.ui.Modifier.size(10.dp))
//                                Text(text = "${readyState.exception}")
//                            }
//                        }
//
//                        else -> {
//                            Box(androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
//                                CircularProgressIndicator()
//                            }
//                        }
//                    }
                }

            }
        }
    }

    @Composable
    private fun Posts(posts: List<PostModel>) {
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
                modifier =  Modifier.width(20.dp).fillMaxHeight(),
                orientation = ScrollBarOrientation.VERTICAL,
                state = ScrollableComponentState.LazyListComponentState(listState)
            )
        }
    }

    @Composable
    private fun Post(post: PostModel) {
        // val context = LocalContext.current
        Column(
            modifier = Modifier
                .clickable {
                    SharedMemory.effectFlow.tryEmit(
                        NavigateEffect(
                            PostDetailsScreen(
                                postUrl = post.link
                            )
                        )
                    )
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
                contentAlignment = Alignment.Center
            ) {
                post.image?.let { imageUrl ->
                    AsyncImage(
                        modifier = Modifier.height(250.dp),
                        url = imageUrl,
                        loadingView = {},
                        errorView = {}
                    )
                }

                TextButton(onClick = {
                    viewModel.removeBookmark(post.id)
                },
                    modifier = Modifier.align(alignment = Alignment.TopEnd)) {
                    Text(text = "Удалить", color = Color.Red)
                }



//                val favIcon = Res.drawable.ic_cell_fav_enabled
//
//                Image(
//                    modifier = Modifier.align(alignment = Alignment.TopEnd)
//                        .clickable {
//                            viewModel.updatePostFavorite(post.link, !post.isFavorite)
//                        },
//                    painter = painterResource(favIcon),
//                    contentDescription = null
//                )
            }

            Spacer(modifier = Modifier.size(8.dp))
            Text(text = post.title.orEmpty(), fontSize = 16.sp)
        }
    }


}