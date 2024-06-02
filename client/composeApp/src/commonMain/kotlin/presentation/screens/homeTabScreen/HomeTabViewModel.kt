package presentation.screens.homeTabScreen

import androidx.compose.runtime.mutableStateOf
import data.repository.FeedRepository
import data.service.FreshApi
import domain.model.PostModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import presentation.base.BaseViewModel
import presentation.model.*

class HomeTabViewModel : BaseViewModel<Unit>() {
    override fun onInitState() {

    }

}