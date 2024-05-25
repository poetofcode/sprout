package presentation.base

import data.repository.RepositoryFactory

data class Config(

    val deviceType: DeviceTypes,

    val viewModelStore: ViewModelStore,

    val repositoryFactory: RepositoryFactory,

) {

    enum class DeviceTypes(val isMobile: Boolean) {
        ANDROID(true),
        DESKTOP(false)
    }

}
