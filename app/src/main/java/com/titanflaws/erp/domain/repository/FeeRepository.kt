package com.titanflaws.erp.domain.repository

import com.titanflaws.erp.data.model.Fee
import com.titanflaws.erp.data.model.FeePayment
import com.titanflaws.erp.utils.Resource

/**
 * Repository interface for fee operations
 */
interface FeeRepository {
    
    /**
     * Get fee by ID
     */
    suspend fun getFeeById(feeId: String): Resource<Fee>
    
    /**
     * Get all fees
     */
    suspend fun getAllFees(): Resource<List<Fee>>
    
    /**
     * Get fees by academic year
     */
    suspend fun getFeesByAcademicYear(academicYearId: String): Resource<List<Fee>>
    
    /**
     * Get fees for a specific class
     */
    suspend fun getFeesByClass(classId: String): Resource<List<Fee>>
    
    /**
     * Create a new fee
     */
    suspend fun createFee(fee: Fee): Resource<Fee>
    
    /**
     * Update an existing fee
     */
    suspend fun updateFee(fee: Fee): Resource<Fee>
    
    /**
     * Delete a fee
     */
    suspend fun deleteFee(feeId: String): Resource<Boolean>
    
    /**
     * Get fee payment by ID
     */
    suspend fun getFeePaymentById(paymentId: String): Resource<FeePayment>
    
    /**
     * Get all fee payments for a student
     */
    suspend fun getPaymentsForStudent(studentId: String): Resource<List<FeePayment>>
    
    /**
     * Get all fee payments for a specific fee
     */
    suspend fun getPaymentsForFee(feeId: String): Resource<List<FeePayment>>
    
    /**
     * Create a new fee payment
     */
    suspend fun createFeePayment(payment: FeePayment): Resource<FeePayment>
    
    /**
     * Update an existing fee payment
     */
    suspend fun updateFeePayment(payment: FeePayment): Resource<FeePayment>
    
    /**
     * Delete a fee payment
     */
    suspend fun deleteFeePayment(paymentId: String): Resource<Boolean>
    
    /**
     * Get summary of fee payments for a student
     */
    suspend fun getStudentFeesSummary(studentId: String): Resource<Map<String, Any>>
    
    /**
     * Get overdue payments for a student
     */
    suspend fun getOverduePayments(studentId: String): Resource<List<FeePayment>>
    
    /**
     * Generate payment receipt
     */
    suspend fun generateReceipt(paymentId: String): Resource<String>
} 