package com.titanflaws.erp.utils

/**
 * A generic class that holds a value with its loading status.
 * @param <T> Type of the resource data
 * @property data The data of the resource
 * @property message The error message in case of error
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * Represents a successful resource with data
     */
    class Success<T>(data: T) : Resource<T>(data)
    
    /**
     * Represents an error resource with an error message and optional data
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    
    /**
     * Represents a loading resource with optional data
     */
    class Loading<T>(data: T? = null) : Resource<T>(data)
} 