package com.titanflaws.erp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.titanflaws.erp.data.datasource.local.dao.FeeDao
import com.titanflaws.erp.data.datasource.local.dao.FeePaymentDao
import com.titanflaws.erp.data.model.Fee
import com.titanflaws.erp.data.model.FeePayment
import com.titanflaws.erp.domain.repository.FeeRepository
import com.titanflaws.erp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * Implementation of the FeeRepository interface that handles fee operations
 * with Firestore and local Room database
 */
class FeeRepositoryImpl @Inject constructor(
    private val feeDao: FeeDao,
    private val feePaymentDao: FeePaymentDao,
    private val firestore: FirebaseFirestore
) : FeeRepository {

    private val feesCollection = firestore.collection("fees")
    private val feePaymentsCollection = firestore.collection("fee_payments")

    override suspend fun getFeeById(feeId: String): Resource<Fee> = withContext(Dispatchers.IO) {
        try {
            val fee = feeDao.getFeeById(feeId).firstOrNull()
            
            if (fee != null) {
                Resource.Success(fee)
            } else {
                // Try fetching from Firestore if not in local DB
                val feeDoc = feesCollection.document(feeId).get().await()
                val remoteFee = feeDoc.toObject(Fee::class.java)
                
                if (remoteFee != null) {
                    // Cache in local database
                    feeDao.insertFee(remoteFee)
                    Resource.Success(remoteFee)
                } else {
                    Resource.Error("Fee not found")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getAllFees(): Resource<List<Fee>> = withContext(Dispatchers.IO) {
        try {
            val fees = feeDao.getAllFees().firstOrNull()
            
            if (!fees.isNullOrEmpty()) {
                Resource.Success(fees)
            } else {
                // Try fetching from Firestore if local DB is empty
                val feesSnapshot = feesCollection.get().await()
                val remoteFees = feesSnapshot.toObjects(Fee::class.java)
                
                // Cache in local database
                if (remoteFees.isNotEmpty()) {
                    feeDao.insertFees(remoteFees)
                }
                
                Resource.Success(remoteFees)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getFeesByAcademicYear(academicYearId: String): Resource<List<Fee>> = withContext(Dispatchers.IO) {
        try {
            val fees = feeDao.getFeesByAcademicYear(academicYearId).firstOrNull()
            
            if (!fees.isNullOrEmpty()) {
                Resource.Success(fees)
            } else {
                // Try fetching from Firestore
                val feesSnapshot = feesCollection
                    .whereEqualTo("academicYearId", academicYearId)
                    .get()
                    .await()
                
                val remoteFees = feesSnapshot.toObjects(Fee::class.java)
                
                // Cache in local database
                if (remoteFees.isNotEmpty()) {
                    feeDao.insertFees(remoteFees)
                }
                
                Resource.Success(remoteFees)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getFeesByClass(classId: String): Resource<List<Fee>> = withContext(Dispatchers.IO) {
        try {
            val fees = feeDao.getFeesByClass(classId).firstOrNull()
            
            if (!fees.isNullOrEmpty()) {
                Resource.Success(fees)
            } else {
                // Try fetching from Firestore
                val feesSnapshot = feesCollection
                    .whereArrayContains("classIds", classId)
                    .get()
                    .await()
                
                val remoteFees = feesSnapshot.toObjects(Fee::class.java)
                
                // Cache in local database
                if (remoteFees.isNotEmpty()) {
                    feeDao.insertFees(remoteFees)
                }
                
                Resource.Success(remoteFees)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun createFee(fee: Fee): Resource<Fee> = withContext(Dispatchers.IO) {
        try {
            // Generate ID if not provided
            val feeId = if (fee.feeId.isBlank()) UUID.randomUUID().toString() else fee.feeId
            val newFee = fee.copy(feeId = feeId, createdAt = Date(), updatedAt = Date())
            
            // Add to Firestore
            feesCollection.document(feeId).set(newFee).await()
            
            // Add to local database
            feeDao.insertFee(newFee)
            
            Resource.Success(newFee)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun updateFee(fee: Fee): Resource<Fee> = withContext(Dispatchers.IO) {
        try {
            val updatedFee = fee.copy(updatedAt = Date())
            
            // Update in Firestore
            feesCollection.document(fee.feeId).set(updatedFee).await()
            
            // Update in local database
            feeDao.updateFee(updatedFee)
            
            Resource.Success(updatedFee)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun deleteFee(feeId: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Delete from Firestore
            feesCollection.document(feeId).delete().await()
            
            // Delete from local database
            feeDao.deleteFeeById(feeId)
            
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getFeePaymentById(paymentId: String): Resource<FeePayment> = withContext(Dispatchers.IO) {
        try {
            val payment = feePaymentDao.getPaymentById(paymentId).firstOrNull()
            
            if (payment != null) {
                Resource.Success(payment)
            } else {
                // Try fetching from Firestore
                val paymentDoc = feePaymentsCollection.document(paymentId).get().await()
                val remotePayment = paymentDoc.toObject(FeePayment::class.java)
                
                if (remotePayment != null) {
                    // Cache in local database
                    feePaymentDao.insertPayment(remotePayment)
                    Resource.Success(remotePayment)
                } else {
                    Resource.Error("Payment not found")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getPaymentsForStudent(studentId: String): Resource<List<FeePayment>> = withContext(Dispatchers.IO) {
        try {
            val payments = feePaymentDao.getPaymentsByStudent(studentId).firstOrNull()
            
            if (!payments.isNullOrEmpty()) {
                Resource.Success(payments)
            } else {
                // Try fetching from Firestore
                val paymentsSnapshot = feePaymentsCollection
                    .whereEqualTo("studentId", studentId)
                    .get()
                    .await()
                
                val remotePayments = paymentsSnapshot.toObjects(FeePayment::class.java)
                
                // Cache in local database
                if (remotePayments.isNotEmpty()) {
                    feePaymentDao.insertPayments(remotePayments)
                }
                
                Resource.Success(remotePayments)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getPaymentsForFee(feeId: String): Resource<List<FeePayment>> = withContext(Dispatchers.IO) {
        try {
            val payments = feePaymentDao.getPaymentsByFee(feeId).firstOrNull()
            
            if (!payments.isNullOrEmpty()) {
                Resource.Success(payments)
            } else {
                // Try fetching from Firestore
                val paymentsSnapshot = feePaymentsCollection
                    .whereEqualTo("feeId", feeId)
                    .get()
                    .await()
                
                val remotePayments = paymentsSnapshot.toObjects(FeePayment::class.java)
                
                // Cache in local database
                if (remotePayments.isNotEmpty()) {
                    feePaymentDao.insertPayments(remotePayments)
                }
                
                Resource.Success(remotePayments)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun createFeePayment(payment: FeePayment): Resource<FeePayment> = withContext(Dispatchers.IO) {
        try {
            // Generate ID if not provided
            val paymentId = if (payment.paymentId.isBlank()) UUID.randomUUID().toString() else payment.paymentId
            val newPayment = payment.copy(
                paymentId = paymentId,
                paymentDate = payment.paymentDate ?: Date(),
                createdAt = Date(),
                updatedAt = Date()
            )
            
            // Add to Firestore
            feePaymentsCollection.document(paymentId).set(newPayment).await()
            
            // Add to local database
            feePaymentDao.insertPayment(newPayment)
            
            Resource.Success(newPayment)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun updateFeePayment(payment: FeePayment): Resource<FeePayment> = withContext(Dispatchers.IO) {
        try {
            val updatedPayment = payment.copy(updatedAt = Date())
            
            // Update in Firestore
            feePaymentsCollection.document(payment.paymentId).set(updatedPayment).await()
            
            // Update in local database
            feePaymentDao.updatePayment(updatedPayment)
            
            Resource.Success(updatedPayment)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun deleteFeePayment(paymentId: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Delete from Firestore
            feePaymentsCollection.document(paymentId).delete().await()
            
            // Delete from local database
            feePaymentDao.deletePaymentById(paymentId)
            
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getStudentFeesSummary(studentId: String): Resource<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            // Get all fees for the student's class
            val student = firestore.collection("students").document(studentId).get().await()
                .toObject(com.titanflaws.erp.data.model.Student::class.java)
            
            val classId = student?.classId
            if (classId == null) {
                return@withContext Resource.Error("Student not assigned to any class")
            }
            
            // Get fees for this class
            val feeResult = getFeesByClass(classId)
            if (feeResult !is Resource.Success) {
                return@withContext Resource.Error("Failed to fetch fees: ${(feeResult as Resource.Error).message}")
            }
            
            val fees = feeResult.data
            
            // Get all payments made by this student
            val paymentsResult = getPaymentsForStudent(studentId)
            if (paymentsResult !is Resource.Success) {
                return@withContext Resource.Error("Failed to fetch payments: ${(paymentsResult as Resource.Error).message}")
            }
            
            val payments = paymentsResult.data
            if (fees != null && payments != null) {
                // Calculate totals
                val totalFees = fees.sumOf { it.amount }
                val totalPaid = payments.sumOf { it.amount }
                val dueAmount = totalFees - totalPaid

                // Count overdue fees
                val now = Date()
                val overdueFees = fees.count { fee ->
                    val dueDay = fee.dueDay ?: 1
                    val payment = payments.find { it.feeId == fee.feeId }
                    payment == null && isOverdue(dueDay, now)
                }

                val summary = mapOf(
                    "totalFees" to totalFees,
                    "totalPaid" to totalPaid,
                    "dueAmount" to dueAmount,
                    "overdueFees" to overdueFees,
                    "paymentHistory" to (payments?.size ?: "")
                )

                Resource.Success(summary)
            } else {
                Resource.Error("No fees found for the class")
            }
            
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getOverduePayments(studentId: String): Resource<List<FeePayment>> = withContext(Dispatchers.IO) {
        try {
            val now = Date()
            val allPayments = feePaymentDao.getPaymentsByStudent(studentId).firstOrNull() ?: emptyList()
            
            val overduePayments = allPayments.filter { payment ->
                payment.dueDate.before(now) && payment.status == "pending"
            }
            
            Resource.Success(overduePayments)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun generateReceipt(paymentId: String): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val paymentResult = getFeePaymentById(paymentId)
            if (paymentResult !is Resource.Success) {
                return@withContext Resource.Error("Payment not found")
            }
            
            val payment = paymentResult.data
            if (payment != null) {
                // Generate a receipt number if not already present
                val receiptNumber =
                    payment.receiptNumber ?: "RCP-${UUID.randomUUID().toString().substring(0, 8)}"

                // Update payment with receipt number if needed
                if (payment.receiptNumber == null) {
                    val updatedPayment = payment.copy(receiptNumber = receiptNumber)
                    updateFeePayment(updatedPayment)
                }

                // Return receipt URL or ID
                Resource.Success(receiptNumber)
            } else {
                Resource.Error("Payment not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }
    
    // Helper function to check if a fee is overdue
    private fun isOverdue(dueDay: Int, currentDate: Date): Boolean {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = currentDate
        return calendar.get(java.util.Calendar.DAY_OF_MONTH) > dueDay
    }
    
    // Sync methods for DataSyncWorker
    
    /**
     * Sync all fees from Firestore to local database
     */
    suspend fun syncFees() {
        try {
            val feesSnapshot = feesCollection.get().await()
            val fees = feesSnapshot.toObjects(Fee::class.java)
            feeDao.insertFees(fees)
            
            val paymentsSnapshot = feePaymentsCollection.get().await()
            val payments = paymentsSnapshot.toObjects(FeePayment::class.java)
            feePaymentDao.insertPayments(payments)
        } catch (e: Exception) {
            // Handle errors
        }
    }
    
    /**
     * Sync fees for a specific student
     */
    suspend fun syncStudentFees(studentId: String) {
        try {
            // Sync payments for this student
            val paymentsSnapshot = feePaymentsCollection
                .whereEqualTo("studentId", studentId)
                .get()
                .await()
                
            val payments = paymentsSnapshot.toObjects(FeePayment::class.java)
            feePaymentDao.insertPayments(payments)
            
            // Also sync fee structures
            val student = firestore.collection("students").document(studentId).get().await()
                .toObject(com.titanflaws.erp.data.model.Student::class.java)
                
            student?.classId?.let { classId ->
                val feesSnapshot = feesCollection
                    .whereArrayContains("classIds", classId)
                    .get()
                    .await()
                    
                val fees = feesSnapshot.toObjects(Fee::class.java)
                feeDao.insertFees(fees)
            }
        } catch (e: Exception) {
            // Handle errors
        }
    }
    
    /**
     * Insert a fee to local database (for DataSyncWorker)
     */
    suspend fun insertFeeToLocal(fee: Fee) {
        feeDao.insertFee(fee)
    }
    
    /**
     * Insert a fee payment to local database (for DataSyncWorker)
     */
    suspend fun insertFeePaymentToLocal(payment: FeePayment) {
        feePaymentDao.insertPayment(payment)
    }
} 