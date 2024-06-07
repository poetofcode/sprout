package data.service

import io.ktor.client.*

interface NetworkingFactory {

    fun createHttpClient() : HttpClient

    fun createApi() : MainApi

}

class NetworkingFactoryImpl : NetworkingFactory {
    
    override fun createHttpClient(): HttpClient {
        return HttpClientFactory(
            baseUrl = BASE_URL,
        ).createClient()
    }

    override fun createApi(): MainApi {
        return MainApi(
            httpClient = createHttpClient(),
            baseUrl = BASE_URL
        )
    }

    private companion object {
        // TODO вынести в buildConfig
        const val BASE_URL = "http://192.168.0.102:3000"
    }

}