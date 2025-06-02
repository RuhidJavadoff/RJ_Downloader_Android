package com.ruhidjavadoff.rjdownloader

import okhttp3.OkHttpClient
import okhttp3.Request as OkHttpRequest // OkHttp Request-i ucun
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response as OkHttpResponse // OkHttp Response-u üçün
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException // Bu import qalır
import java.io.IOException
import java.util.concurrent.TimeUnit

class CustomDownloader private constructor(private val client: OkHttpClient) : Downloader() {

    companion object {
        @Volatile
        private var instance: CustomDownloader? = null

        fun getInstance(): CustomDownloader {
            return instance ?: synchronized(this) {
                instance ?: CustomDownloader(
                    OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(30, TimeUnit.SECONDS)

                        .build()
                ).also { instance = it }
            }
        }
    }

    @Throws(IOException::class, ReCaptchaException::class)
    override fun execute(request: Request): Response {
        val httpMethod = request.httpMethod()
        val url = request.url()
        val headers = request.headers()
        val dataToSend = request.dataToSend()

        val okHttpRequestBuilder = OkHttpRequest.Builder().url(url)

        headers?.forEach { (headerName, headerValueList) ->
            headerValueList?.forEach { headerValue ->
                if (headerValue != null) {
                    okHttpRequestBuilder.addHeader(headerName, headerValue)
                }
            }
        }

        val body = dataToSend?.toRequestBody(null) // Content-Type null ola bilər
        okHttpRequestBuilder.method(httpMethod, body)

        val okHttpRequest = okHttpRequestBuilder.build()
        val okHttpResponse: OkHttpResponse = client.newCall(okHttpRequest).execute()

        val responseBodyString = okHttpResponse.body?.string() ?: ""

        val finalUrl = okHttpResponse.request.url.toString()

        return Response(
            okHttpResponse.code,
            okHttpResponse.message,
            okHttpResponse.headers.toMultimap(), // OkHttp Headers -> Map<String, List<String>>
            responseBodyString,
            finalUrl
        )
    }
}