package presentation.factories

import data.repository.FeedRepository
import data.repository.ProfileRepository
import data.repository.RepositoryFactory
import presentation.base.ViewModelFactory
import presentation.screens.homeTabScreen.HomeTabViewModel
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


class ProfileTabViewModelFactory(val profileRepository: ProfileRepository)
    : ViewModelFactory<ProfileTabViewModel> {
    override fun createViewModel(): ProfileTabViewModel {
        return ProfileTabViewModel(profileRepository)
    }

    override val vmTypeName: String
        get() = ProfileTabViewModel::class.java.typeName

}


fun viewModelFactories(
    repositoryFactory: RepositoryFactory
): List<ViewModelFactory<*>> {
    return listOf<ViewModelFactory<*>>(
        HomeTabViewModelFactory(),
        PostListViewModelFactory(repositoryFactory.createFeedRepository()),
        ProfileTabViewModelFactory(repositoryFactory.createProfileRepository()),
    )
}