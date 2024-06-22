package com.poetofcode.SproutClient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.poetofcode.sproutclient.retrieveToken
import data.repository.RepositoryFactoryImpl
import data.service.NetworkingFactory
import data.service.NetworkingFactoryImpl
import data.utils.ProfileStorageImpl
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import presentation.App
import presentation.base.Config
import presentation.base.ViewModelStore
import presentation.factories.*
import presentation.navigation.SetBackHandlerEffect
import presentation.navigation.SharedMemory
import specific.AndroidContentProvider


class MainActivity : ComponentActivity() {
    // val repositoryFactory = MockRepositoryFactory()
    val profileStorage = ProfileStorageImpl(
        AndroidContentProvider(
            fileName = "sessioncache.json",
            context = this,
        )
    )

    val networkingFactory: NetworkingFactory = NetworkingFactoryImpl(profileStorage)

    val repositoryFactory = RepositoryFactoryImpl(
        api = networkingFactory.createApi(),
        profileStorage = profileStorage
    )

    private var backHandleCallback: (() -> Boolean)? = null

    val vmStoreImpl = ViewModelStore(
        coroutineScope = lifecycleScope,
        vmFactories = viewModelFactories(
            repositoryFactory = repositoryFactory
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        baseContext.retrieveToken()

        setContent {
            App(
                Config(
                    deviceType = Config.DeviceTypes.ANDROID,
                    viewModelStore = vmStoreImpl,
                    repositoryFactory = repositoryFactory,
                )
            )
        }

        lifecycleScope.launch {
            SharedMemory.effectFlow
                .onEach { effect ->
                    if (effect is SetBackHandlerEffect) {
                        backHandleCallback = effect.cb
                    }
                }.launchIn(this)
        }
    }

    override fun onBackPressed() {
        backHandleCallback?.let { cb ->
            if (cb()) {
                return
            } else {
                return@let
            }
        }
        super.onBackPressed()
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
//
//    val vmStoreImpl = ViewModelStore(
//        coroutineScope = rememberCoroutineScope(),
//        vmFactories = viewModelFactories
//    )
//    App(Config(viewModelStore = vmStoreImpl))
}