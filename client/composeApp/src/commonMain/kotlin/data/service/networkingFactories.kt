package data.service

import data.utils.ProfileStorage
import io.ktor.client.*

interface NetworkingFactory {

    fun createHttpClient() : HttpClient

    fun createApi() : MainApi

}

class NetworkingFactoryImpl(
    private val profileStorage: ProfileStorage,
) : NetworkingFactory {
    
    override fun createHttpClient(): HttpClient {
        return HttpClientFactory(
            baseUrl = BASE_URL,
        ).createClient()
    }

    override fun createApi(): MainApi {
        return MainApi(
            httpClient = createHttpClient(),
            baseUrl = BASE_URL,
            profileStorage = profileStorage,
        )
    }

    private companion object {
        // TODO вынести в buildConfig
        const val BASE_URL = "http://192.168.0.108:3000"
    }

}