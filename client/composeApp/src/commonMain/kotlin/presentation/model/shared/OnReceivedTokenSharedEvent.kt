package presentation.model.shared

import presentation.navigation.SharedEvent

data class OnReceivedTokenSharedEvent(
    val token: String
) : SharedEvent
