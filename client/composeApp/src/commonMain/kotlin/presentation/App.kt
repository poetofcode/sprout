package presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.Tabs.*
import presentation.base.Config
import presentation.navigation.NavStateImpl
import presentation.navigation.Navigator
import presentation.screens.bookmarkTabScreen.BookmarkTabScreen
import presentation.screens.homeTabScreen.HomeTabScreen
import presentation.screens.profileTabScreen.ProfileTabScreen
import sproutclient.composeapp.generated.resources.Res
import sproutclient.composeapp.generated.resources.ic_fav_tab
import sproutclient.composeapp.generated.resources.ic_home_tab
import sproutclient.composeapp.generated.resources.ic_profile_tab


const val VERTICAL_PANEL_SIZE = 60
const val VERTICAL_ICON_SIZE = 30
const val HORIZONTAL_PANEL_SIZE = 60
const val HORIZONTAL_ICON_SIZE = 50

data class MainAppState(
    val isMenuVisible: MutableState<Boolean> = mutableStateOf(false)
)

val LocalMainAppState = staticCompositionLocalOf<MainAppState> {
    throw RuntimeException("LocalMainAppState not set")
}

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App(config: Config) {
    CompositionLocalProvider(LocalMainAppState provides MainAppState()) {
        MaterialTheme {
            val selectedTab = remember { mutableStateOf<Tabs>(Tabs.HOME) }
            val navState = remember {
                NavStateImpl(viewModelStore = config.viewModelStore).apply {
                    push(HomeTabScreen())
                    push(BookmarkTabScreen())
                    push(ProfileTabScreen())
                }
            }

            AppLayout(
                deviceType = config.deviceType,
                menu = Menu(
                    tabs = Tabs.entries,
                    onTabClick = { tab ->
                        selectedTab.value = tab
                    },
                    itemContent = { tab ->
                        val isSelected = selectedTab.value == tab
                        val iconSize = if (config.deviceType.isMobile) {
                            HORIZONTAL_ICON_SIZE.dp
                        } else {
                            VERTICAL_ICON_SIZE.dp
                        }
                        Box(Modifier.size(iconSize), contentAlignment = Alignment.Center) {
                            val icon = when (tab) {
                                HOME -> Res.drawable.ic_home_tab
                                PROFILE -> Res.drawable.ic_profile_tab
                                BOOKMARK -> Res.drawable.ic_fav_tab
                            }

                            Image(
                                painter = painterResource(icon),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(
                                    if (isSelected) {
                                        MaterialTheme.colors.primary
                                    } else {
                                        Color.Gray
                                    }
                                )
                            )
                        }
                    }
                ),
            ) {
                Navigator(
                    modifier = Modifier.fillMaxWidth(),
                    state = navState
                )
            }

            LaunchedEffect(selectedTab.value) {
                navState.moveToFront(selectedTab.value.key)
            }
        }
    }
}

@Composable
fun AppLayout(
    deviceType: Config.DeviceTypes,
    menu: Menu,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    val isMenuVisible = LocalMainAppState.current.isMenuVisible.value

    if (deviceType.isMobile) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.weight(1f)) {
                content()
            }

            if (isMenuVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.height(HORIZONTAL_PANEL_SIZE.dp),
                        horizontalArrangement = Arrangement.spacedBy((HORIZONTAL_ICON_SIZE / 3).dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        menu.tabs.forEach { tab ->
                            Box(Modifier.clickable { menu.onTabClick(tab) }) {
                                menu.itemContent(tab)
                            }
                        }
                    }
                }
            }
        }
    } else {
        Row(
            modifier = modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(VERTICAL_PANEL_SIZE.dp)
                    .background(Color.LightGray),
            ) {
                menu.tabs.forEach { tab ->
                    Box(
                        modifier = Modifier.clickable { menu.onTabClick(tab) }.size(60.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        menu.itemContent(tab)
                    }
                }
            }

            Box(Modifier.weight(1f)) {
                content()
            }
        }
    }
}

data class Menu(
    val tabs: List<Tabs>,
    val onTabClick: (Tabs) -> Unit,
    val itemContent: @Composable (tab: Tabs) -> Unit
)


enum class Tabs(val key: String) {
    HOME("tab_home"),
    BOOKMARK("tab_bookmark"),
    PROFILE("tab_profile"),
}