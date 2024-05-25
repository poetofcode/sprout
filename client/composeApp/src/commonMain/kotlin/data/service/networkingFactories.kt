package data.service

import io.ktor.client.*

interface NetworkingFactory {

    fun createHttpClient() : HttpClient

    fun createApi() : FreshApi

}

class NetworkingFactoryImpl : NetworkingFactory {
    
    override fun createHttpClient(): HttpClient {
        return HttpClientFactory(
            baseUrl = BASE_URL,
            apiKey = API_KEY
        ).createClient()
    }

    override fun createApi(): FreshApi {
        return FreshApi(
            httpClient = createHttpClient(),
            baseUrl = BASE_URL
        )
    }

    private companion object {
        // TODO вынести в buildConfig
        const val BASE_URL = "http://91.215.153.157:8080"
        const val API_KEY = "secret-api-key"
    }

}