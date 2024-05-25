package data.service

import data.entity.DataResponse
import data.entity.TokenResponse
import data.utils.buildEndpoint
import data.utils.toSha1
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class HttpClientFactory(
    val baseUrl: String,
    val apiKey: String,
) {
    
    fun createClient() : HttpClient {
        val bearerTokenStorage = mutableListOf<BearerTokens>()
        
        fun calculateToken(path: String, remoteToken: String, salt: String) : String {
            return (path.toSha1() + remoteToken.toSha1() + salt.toSha1()).toSha1()
        }

        bearerTokenStorage.add(BearerTokens("", ""))

        return HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
                // filter { request ->
                //    request.url.host.contains("ktor.io")
                // }
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        bearerTokenStorage.last()
                    }

                    refreshTokens { // this: RefreshTokensParams
                        try {
                            val tokenResponse: DataResponse<TokenResponse> = client.post("/site/token".buildEndpoint(baseUrl)).body()
                            val remoteToken : String = tokenResponse.result.token!!

                            val newToken : String = run {
                                val path = response.request.url.encodedPath
                                val t = calculateToken(path, remoteToken, apiKey)
                                t
                            }
                            bearerTokenStorage.add(BearerTokens(newToken, remoteToken))
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }

                        bearerTokenStorage.last()
                    }
                }
            }
        }
    }
    
}