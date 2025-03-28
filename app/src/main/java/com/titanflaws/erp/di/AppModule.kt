package com.titanflaws.erp.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import com.titanflaws.erp.data.datasource.local.ERPDatabase
import com.titanflaws.erp.utils.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Firebase Auth instance
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    // Firebase Firestore instance
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    // Firebase Storage instance
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    // Firebase Functions instance
    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions = FirebaseFunctions.getInstance()

    // Firebase Messaging instance
    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()

    // Room Database instance
    @Provides
    @Singleton
    fun provideERPDatabase(
        @ApplicationContext context: Context
    ): ERPDatabase = Room.databaseBuilder(
        context,
        ERPDatabase::class.java,
        "erp_database"
    )
    .fallbackToDestructiveMigration()
    .build()

    // Provide DAO's from the database
    @Provides
    @Singleton
    fun provideUserDao(database: ERPDatabase) = database.userDao()

    @Provides
    @Singleton
    fun provideStudentDao(database: ERPDatabase) = database.studentDao()

    @Provides
    @Singleton
    fun provideTeacherDao(database: ERPDatabase) = database.teacherDao()

    @Provides
    @Singleton
    fun provideAttendanceDao(database: ERPDatabase) = database.attendanceDao()

    @Provides
    @Singleton
    fun provideExamDao(database: ERPDatabase) = database.examDao()

    @Provides
    @Singleton
    fun provideFeeDao(database: ERPDatabase) = database.feeDao()

    // Additional dependencies
    @Provides
    @Singleton
    fun provideTokenRepository(firestore: FirebaseFirestore, auth: FirebaseAuth): TokenRepository {
        return object : TokenRepository {
            override suspend fun updateToken(token: String) {
                val userId = auth.currentUser?.uid ?: return
                firestore.collection("users")
                    .document(userId)
                    .update("fcmToken", token)
            }
        }
    }
} 