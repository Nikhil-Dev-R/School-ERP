package com.titanflaws.erp.data.datasource.remote.interceptor

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor to add authorization headers to requests
 */
@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {
    
    @Inject
    lateinit var auth: FirebaseAuth
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip if it's an auth request that doesn't need a token
        if (originalRequest.url.toString().contains("/auth/")) {
            return chain.proceed(originalRequest)
        }
        
        val currentUser = auth.currentUser
        
        // Get ID token for authenticated user
        val token = runBlocking {
            try {
                currentUser?.getIdToken(false)?.result?.token
            } catch (e: Exception) {
                null
            }
        }
        
        // Add authorization header if we have a token
        val modifiedRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(modifiedRequest)
    }
} 