package presentation.factories

import data.repository.BookmarkRepository
import data.repository.FeedRepository
import data.repository.RepositoryFactory
import presentation.base.ViewModelFactory
import presentation.screens.bookmarkTabScreen.BookmarkTabViewModel
import presentation.screens.homeTabScreen.HomeTabViewModel
import presentation.screens.postDetailsScreen.PostDetailsViewModel
import presentation.screens.postListScreen.PostListViewModel

class HomeTabViewModelFactory() : ViewModelFactory<HomeTabViewModel> {
    override fun createViewModel(): HomeTabViewModel {
        return HomeTabViewModel()
    }

    override val vmTypeName: String
        get() = HomeTabViewModel::class.java.typeName

}

class PostListViewModelFactory(val feedRepository: FeedRepository,
                               val bookmarRepository: BookmarkRepository)
    : ViewModelFactory<PostListViewModel> {
    override fun createViewModel(): PostListViewModel {
        return PostListViewModel(feedRepository = feedRepository, bookmarkRepository = bookmarRepository)
    }

    override val vmTypeName: String
        get() = PostListViewModel::class.java.typeName

}

class PostDetailsViewModelFactory() : ViewModelFactory<PostDetailsViewModel> {
    override fun createViewModel(): PostDetailsViewModel {
        return PostDetailsViewModel()
    }

    override val vmTypeName: String
        get() = PostDetailsViewModel::class.java.typeName

}

class BookmarkTabViewModelFactory(val bookmarkRepository: BookmarkRepository)
    : ViewModelFactory<BookmarkTabViewModel> {
    override fun createViewModel(): BookmarkTabViewModel {
        return BookmarkTabViewModel(bookmarkRepository = bookmarkRepository)
    }

    override val vmTypeName: String
        get() = BookmarkTabViewModel::class.java.typeName

}

fun viewModelFactories(
    repositoryFactory: RepositoryFactory
): List<ViewModelFactory<*>> {
    val bookmarkRepository = repositoryFactory.createBookmarkRepository()
    return listOf<ViewModelFactory<*>>(
        HomeTabViewModelFactory(),
        PostListViewModelFactory(repositoryFactory.createFeedRepository(),
            bookmarkRepository),
        PostDetailsViewModelFactory(),
        BookmarkTabViewModelFactory(bookmarkRepository)
    )
}