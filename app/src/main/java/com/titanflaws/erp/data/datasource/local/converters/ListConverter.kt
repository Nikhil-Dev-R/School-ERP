package com.titanflaws.erp.data.datasource.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type converter for Room database to convert between List and String
 */
class ListConverter {
    private val gson = Gson()
    
    /**
     * Convert from List<String> to String for storage in SQLite
     */
    @TypeConverter
    fun fromStringListToString(list: List<String>?): String? {
        return list?.let { gson.toJson(it) }
    }
    
    /**
     * Convert from String to List<String> when reading from SQLite
     */
    @TypeConverter
    fun fromStringToStringList(value: String?): List<String>? {
        if (value == null) return null
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
    
    /**
     * Convert from List<Int> to String
     */
    @TypeConverter
    fun fromIntListToString(list: List<Int>?): String? {
        return list?.let { gson.toJson(it) }
    }
    
    /**
     * Convert from String to List<Int>
     */
    @TypeConverter
    fun fromStringToIntList(value: String?): List<Int>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, listType)
    }
    
    /**
     * Convert from List<Float> to String
     */
    @TypeConverter
    fun fromFloatListToString(list: List<Float>?): String? {
        return list?.let { gson.toJson(it) }
    }
    
    /**
     * Convert from String to List<Float>
     */
    @TypeConverter
    fun fromStringToFloatList(value: String?): List<Float>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Float>>() {}.type
        return gson.fromJson(value, listType)
    }
    
    /**
     * Convert from List<Boolean> to String
     */
    @TypeConverter
    fun fromBooleanListToString(list: List<Boolean>?): String? {
        return list?.let { gson.toJson(it) }
    }
    
    /**
     * Convert from String to List<Boolean>
     */
    @TypeConverter
    fun fromStringToBooleanList(value: String?): List<Boolean>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Boolean>>() {}.type
        return gson.fromJson(value, listType)
    }
    
    /**
     * Convert from List<Long> to String
     */
    @TypeConverter
    fun fromLongListToString(list: List<Long>?): String? {
        return list?.let { gson.toJson(it) }
    }
    
    /**
     * Convert from String to List<Long>
     */
    @TypeConverter
    fun fromStringToLongList(value: String?): List<Long>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(value, listType)
    }
} 