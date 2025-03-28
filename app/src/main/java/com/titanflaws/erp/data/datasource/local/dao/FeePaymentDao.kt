package com.titanflaws.erp.data.datasource.local.dao

import androidx.room.*
import com.titanflaws.erp.data.model.FeePayment
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Data Access Object for the FeePayment entity
 */
@Dao
interface FeePaymentDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: FeePayment)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<FeePayment>)
    
    @Update
    suspend fun updatePayment(payment: FeePayment)
    
    @Delete
    suspend fun deletePayment(payment: FeePayment)
    
    @Query("DELETE FROM fee_payments WHERE paymentId = :paymentId")
    suspend fun deletePaymentById(paymentId: String)
    
    @Query("SELECT * FROM fee_payments WHERE paymentId = :paymentId")
    fun getPaymentById(paymentId: String): Flow<FeePayment?>
    
    @Query("SELECT * FROM fee_payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<FeePayment>>
    
    @Query("SELECT * FROM fee_payments WHERE studentId = :studentId ORDER BY paymentDate DESC")
    fun getPaymentsByStudent(studentId: String): Flow<List<FeePayment>>
    
    @Query("SELECT * FROM fee_payments WHERE feeId = :feeId ORDER BY paymentDate DESC")
    fun getPaymentsByFee(feeId: String): Flow<List<FeePayment>>
    
    @Query("SELECT * FROM fee_payments WHERE invoiceNumber = :invoiceNumber")
    fun getPaymentByInvoice(invoiceNumber: String): Flow<FeePayment?>
    
    @Query("SELECT * FROM fee_payments WHERE receiptNumber = :receiptNumber")
    fun getPaymentByReceipt(receiptNumber: String): Flow<FeePayment?>
    
    @Query("SELECT * FROM fee_payments WHERE status = :status ORDER BY paymentDate DESC")
    fun getPaymentsByStatus(status: String): Flow<List<FeePayment>>
    
    @Query("SELECT * FROM fee_payments WHERE paymentMethod = :method ORDER BY paymentDate DESC")
    fun getPaymentsByMethod(method: String): Flow<List<FeePayment>>
    
    @Query("SELECT * FROM fee_payments WHERE dueDate < :date AND status != 'PAID' ORDER BY dueDate")
    fun getOverduePayments(date: Date): Flow<List<FeePayment>>
    
    @Query("SELECT SUM(amount) FROM fee_payments WHERE studentId = :studentId AND status = 'PAID'")
    suspend fun getTotalPaidAmount(studentId: String): Double?
    
    @Query("SELECT SUM(amount) FROM fee_payments WHERE feeId = :feeId AND status = 'PAID'")
    suspend fun getTotalPaidAmountForFee(feeId: String): Double?
    
    @Query("SELECT * FROM fee_payments WHERE paymentDate BETWEEN :startDate AND :endDate ORDER BY paymentDate DESC")
    fun getPaymentsByDateRange(startDate: Date, endDate: Date): Flow<List<FeePayment>>
} 