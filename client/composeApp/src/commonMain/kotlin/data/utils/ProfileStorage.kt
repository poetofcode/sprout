package data.utils

import domain.model.Profile
import kotlinx.serialization.encodeToString

interface ProfileStorage {

    fun save(profile: Profile)

    fun load() : Profile?

}

class ProfileStorageImpl(
    private val contentProvider: ContentProvider,
) : ProfileStorage {

    private val json = JsonProvider.json

    override fun save(profile: Profile) {
        try {
            val profileContent = json.encodeToString<Profile>(profile)
            contentProvider.saveContent(profileContent)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun load(): Profile? {
        try {
            val fileContent = contentProvider.provideContent()
            val profile = json.decodeFromString<Profile>(fileContent)
            return profile
        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        }
    }

}