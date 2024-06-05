package data.service

import data.entity.*
import data.utils.buildEndpoint
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class MainApi(
    val httpClient: HttpClient,
    val baseUrl: String
) {

//    suspend fun fetchFeed(): ResultResponse<FeedResponse> = parseRequestResult<FeedResponse> {
//        httpClient.get("/site/fresh/feed".buildEndpoint(baseUrl))
//    }


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

    private suspend inline fun <reified T : Any> HttpResponse.tryParseFailure(): FailureResponse<T>? = try {
        body<FailureResponse<T>>()
    } catch (e: Throwable) {
        null
    }

}