package presentation.factories

import data.repository.FeedRepository
import data.repository.ProfileRepository
import data.repository.RepositoryFactory
import presentation.base.ViewModelFactory
import presentation.screens.authScreen.AuthViewModel
import presentation.screens.homeTabScreen.HomeTabViewModel
import presentation.screens.profileScreen.ProfileViewModel
import presentation.screens.profileTabScreen.ProfileTabViewModel
import presentation.screens.startScreen.StartViewModel

class HomeTabViewModelFactory() : ViewModelFactory<HomeTabViewModel> {
    override fun createViewModel(): HomeTabViewModel {
        return HomeTabViewModel()
    }

    override val vmTypeName: String
        get() = HomeTabViewModel::class.java.typeName

}

class PostListViewModelFactory(val feedRepository: FeedRepository)
    : ViewModelFactory<StartViewModel> {
    override fun createViewModel(): StartViewModel {
        return StartViewModel(feedRepository = feedRepository)
    }

    override val vmTypeName: String
        get() = StartViewModel::class.java.typeName

}


class ProfileTabViewModelFactory
    : ViewModelFactory<ProfileTabViewModel> {
    override fun createViewModel(): ProfileTabViewModel {
        return ProfileTabViewModel()
    }

    override val vmTypeName: String
        get() = ProfileTabViewModel::class.java.typeName

}

class ProfileViewModelFactory(val profileRepository: ProfileRepository)
    : ViewModelFactory<ProfileViewModel> {
    override fun createViewModel(): ProfileViewModel {
        return ProfileViewModel(profileRepository)
    }

    override val vmTypeName: String
        get() = ProfileViewModel::class.java.typeName

}

class AuthViewModelFactory(private val profileRepository: ProfileRepository)
    : ViewModelFactory<AuthViewModel> {
    override fun createViewModel(): AuthViewModel {
        return AuthViewModel(profileRepository)
    }

    override val vmTypeName: String
        get() = AuthViewModel::class.java.typeName

}


fun viewModelFactories(
    repositoryFactory: RepositoryFactory
): List<ViewModelFactory<*>> {
    val profileRepository = repositoryFactory.createProfileRepository()
    return listOf<ViewModelFactory<*>>(
        HomeTabViewModelFactory(),
        PostListViewModelFactory(repositoryFactory.createFeedRepository()),
        ProfileTabViewModelFactory(),
        ProfileViewModelFactory(profileRepository),
        AuthViewModelFactory(profileRepository),
    )
}