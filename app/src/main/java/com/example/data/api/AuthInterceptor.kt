package com.example.data.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("user-agent", ApiConfig.userAgent)
            .header("referer", ApiConfig.referer)
            .header("origin", ApiConfig.origin)
            .header("accept", "application/json")
            .header("x-u-aid", ApiConfig.xUAid)
            .header("x-client-info", ApiConfig.clientInfo)
            .header("x-request-lang", ApiConfig.requestLang)
            .header("x-vip-restrict", ApiConfig.vipRestrict)
            .header("x-no-high-risk-restrict", ApiConfig.noHighRiskRestrict)

        val token = ApiConfig.authToken
        if (token.isNotBlank()) {
            val formattedToken = if (token.startsWith("Bearer ", ignoreCase = true)) token else "Bearer $token"
            requestBuilder.header("authorization", formattedToken)
        }

        val response = chain.proceed(requestBuilder.build())
        return response
    }
}
