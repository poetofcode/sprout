package presentation.factories

import data.repository.FeedRepository
import data.repository.RepositoryFactory
import presentation.base.ViewModelFactory
import presentation.screens.homeTabScreen.HomeTabViewModel
import presentation.screens.postDetailsScreen.PostDetailsViewModel
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

class PostDetailsViewModelFactory() : ViewModelFactory<PostDetailsViewModel> {
    override fun createViewModel(): PostDetailsViewModel {
        return PostDetailsViewModel()
    }

    override val vmTypeName: String
        get() = PostDetailsViewModel::class.java.typeName

}

fun viewModelFactories(
    repositoryFactory: RepositoryFactory
): List<ViewModelFactory<*>> {
    return listOf<ViewModelFactory<*>>(
        HomeTabViewModelFactory(),
        PostListViewModelFactory(repositoryFactory.createFeedRepository()),
        PostDetailsViewModelFactory(),
    )
}