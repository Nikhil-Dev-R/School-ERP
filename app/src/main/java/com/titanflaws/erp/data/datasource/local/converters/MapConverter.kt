package com.titanflaws.erp.data.datasource.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type converter for Room database to convert between Map and String
 */
class MapConverter {
    private val gson = Gson()
    
    /**
     * Convert from Map to String for storage in SQLite
     */
    @TypeConverter
    fun fromMapToString(map: Map<String, Any>?): String? {
        return map?.let { gson.toJson(it) }
    }
    
    /**
     * Convert from String to Map when reading from SQLite
     */
    @TypeConverter
    fun fromStringToMap(value: String?): Map<String, Any>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        return gson.fromJson(value, mapType)
    }
    
    /**
     * Convert from Map<String, Float> to String
     */
    @TypeConverter
    fun fromFloatMapToString(map: Map<String, Float>?): String? {
        return map?.let { gson.toJson(it) }
    }
    
    /**
     * Convert from String to Map<String, Float>
     */
    @TypeConverter
    fun fromStringToFloatMap(value: String?): Map<String, Float>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<String, Float>>() {}.type
        return gson.fromJson(value, mapType)
    }
    
    /**
     * Convert from Map<String, Boolean> to String
     */
    @TypeConverter
    fun fromBooleanMapToString(map: Map<String, Boolean>?): String? {
        return map?.let { gson.toJson(it) }
    }
    
    /**
     * Convert from String to Map<String, Boolean>
     */
    @TypeConverter
    fun fromStringToBooleanMap(value: String?): Map<String, Boolean>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<String, Boolean>>() {}.type
        return gson.fromJson(value, mapType)
    }
    
    /**
     * Convert from Map<String, String> to String
     */
    @TypeConverter
    fun fromStringMapToString(map: Map<String, String>?): String? {
        return map?.let { gson.toJson(it) }
    }
    
    /**
     * Convert from String to Map<String, String>
     */
    @TypeConverter
    fun fromStringToStringMap(value: String?): Map<String, String>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType)
    }
    
    /**
     * Convert from Map<String, List<String>> to String
     */
    @TypeConverter
    fun fromListMapToString(map: Map<String, List<String>>?): String? {
        return map?.let { gson.toJson(it) }
    }
    
    /**
     * Convert from String to Map<String, List<String>>
     */
    @TypeConverter
    fun fromStringToListMap(value: String?): Map<String, List<String>>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<String, List<String>>>() {}.type
        return gson.fromJson(value, mapType)
    }
} 