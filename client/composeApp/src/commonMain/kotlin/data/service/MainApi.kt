package data.service

import data.entity.CreateSessionRequestBody
import data.entity.CreateSessionResponse
import data.entity.DataResponse
import data.entity.ExceptionResponse
import data.entity.FailureResponse
import data.entity.ResultResponse
import data.utils.ProfileStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path

class MainApi(
    private val httpClient: HttpClient,
    private val profileStorage: ProfileStorage,
) {

//    suspend fun fetchFeed(): ResultResponse<FeedResponse> = parseRequestResult<FeedResponse> {
//        httpClient.get("/site/fresh/feed".buildEndpoint(baseUrl))
//    }

    suspend fun createSession(requestBody: CreateSessionRequestBody): ResultResponse<CreateSessionResponse> =
        parseRequestResult<CreateSessionResponse> {
            httpClient.post {
                nonAuthBlock {
                    url { path("/api/v1/sessions") }
                    setBody(requestBody)
                }
            }
        }

    suspend fun deleteSession(token: String) {
        httpClient.delete {
            authBlock {
                url { path("/api/v1/sessions/$token") }
            }
        }
    }

    private suspend inline fun <reified T : Any> parseRequestResult(doRequest: () -> HttpResponse): ResultResponse<T> {
        var response: HttpResponse? = null
        val parsed: ResultResponse<T> = try {
            response = doRequest()
            response.body<DataResponse<T>>()
        } catch (e: Throwable) {
            response?.tryParseFailure() ?: ExceptionResponse(e)
        }
        return parsed
    }

    private suspend inline fun <reified T : Any> HttpResponse.tryParseFailure(): FailureResponse<T>? =
        try {
            body<FailureResponse<T>>()
        } catch (e: Throwable) {
            null
        }

    private fun HttpRequestBuilder.nonAuthBlock(block: HttpRequestBuilder.() -> Unit) {
        contentType(ContentType.Application.Json)
        block()
    }

    private fun HttpRequestBuilder.authBlock(block: HttpRequestBuilder.() -> Unit) {
        nonAuthBlock {
            profileStorage.load()?.let {
                bearerAuth(it.token)
            }
        }
        block()
    }

}