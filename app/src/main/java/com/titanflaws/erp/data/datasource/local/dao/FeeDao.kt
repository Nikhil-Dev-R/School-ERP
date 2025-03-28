package com.titanflaws.erp.data.datasource.local.dao

import androidx.room.*
import com.titanflaws.erp.data.model.Fee
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Fee entity
 */
@Dao
interface FeeDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFee(fee: Fee)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFees(fees: List<Fee>)
    
    @Update
    suspend fun updateFee(fee: Fee)
    
    @Delete
    suspend fun deleteFee(fee: Fee)
    
    @Query("DELETE FROM fees WHERE feeId = :feeId")
    suspend fun deleteFeeById(feeId: String)
    
    @Query("SELECT * FROM fees WHERE feeId = :feeId")
    fun getFeeById(feeId: String): Flow<Fee?>
    
    @Query("SELECT * FROM fees ORDER BY createdAt DESC")
    fun getAllFees(): Flow<List<Fee>>
    
    @Query("SELECT * FROM fees WHERE academicYearId = :academicYearId ORDER BY createdAt DESC")
    fun getFeesByAcademicYear(academicYearId: String): Flow<List<Fee>>
    
    @Query("SELECT * FROM fees WHERE :classId IN (classIds) ORDER BY createdAt DESC")
    fun getFeesByClass(classId: String): Flow<List<Fee>>
    
    @Query("SELECT * FROM fees WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveFees(): Flow<List<Fee>>
    
    @Query("SELECT COUNT(*) FROM fees")
    suspend fun getFeeCount(): Int
    
    @Query("SELECT * FROM fees WHERE isOptional = 0 ORDER BY createdAt DESC")
    fun getMandatoryFees(): Flow<List<Fee>>
    
    @Query("SELECT * FROM fees WHERE isOptional = 1 ORDER BY createdAt DESC")
    fun getOptionalFees(): Flow<List<Fee>>
    
    @Query("SELECT * FROM fees WHERE frequency = :frequency ORDER BY createdAt DESC")
    fun getFeesByFrequency(frequency: String): Flow<List<Fee>>
} 