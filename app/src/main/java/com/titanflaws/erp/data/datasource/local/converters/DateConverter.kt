package com.titanflaws.erp.data.datasource.local.converters

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converter for Room Database to convert Date objects to Long and back
 */
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
} 