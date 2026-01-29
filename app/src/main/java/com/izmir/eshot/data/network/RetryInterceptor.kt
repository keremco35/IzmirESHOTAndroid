package com.izmir.eshot.data.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * OkHttp interceptor that retries failed requests with exponential backoff.
 * Handles common network errors like timeouts, connection failures, and 5xx server errors.
 */
class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val initialDelayMs: Long = 1000L,
    private val maxDelayMs: Long = 10000L
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var lastException: IOException? = null

        for (attempt in 0..maxRetries) {
            try {
                response?.close()
                response = chain.proceed(request)

                // Success or client error (4xx) - don't retry
                if (response.isSuccessful || response.code in 400..499) {
                    return response
                }

                // Server error (5xx) - retry with backoff
                if (response.code >= 500 && attempt < maxRetries) {
                    val delay = calculateDelay(attempt)
                    Thread.sleep(delay)
                    continue
                }

                return response

            } catch (e: IOException) {
                lastException = e

                // Don't retry if it's the last attempt
                if (attempt >= maxRetries) {
                    break
                }

                // Check if error is retryable
                if (isRetryableError(e)) {
                    val delay = calculateDelay(attempt)
                    Thread.sleep(delay)
                } else {
                    throw e
                }
            }
        }

        throw lastException ?: IOException("Request failed after $maxRetries retries")
    }

    /**
     * Determines if an exception should trigger a retry.
     */
    private fun isRetryableError(e: IOException): Boolean {
        return when (e) {
            is SocketTimeoutException -> true
            is UnknownHostException -> true
            else -> e.message?.contains("connection", ignoreCase = true) == true ||
                    e.message?.contains("timeout", ignoreCase = true) == true
        }
    }

    /**
     * Calculates exponential backoff delay with jitter.
     */
    private fun calculateDelay(attempt: Int): Long {
        val exponentialDelay = initialDelayMs * (1 shl attempt) // 2^attempt
        val jitter = (Math.random() * 1000).toLong()
        return minOf(exponentialDelay + jitter, maxDelayMs)
    }
}
