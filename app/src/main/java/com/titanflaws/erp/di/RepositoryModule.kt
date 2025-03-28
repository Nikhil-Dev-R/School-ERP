package com.titanflaws.erp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.titanflaws.erp.data.datasource.local.dao.*
import com.titanflaws.erp.data.datasource.remote.api.AuthApi
import com.titanflaws.erp.data.datasource.remote.api.ExamApi
import com.titanflaws.erp.data.datasource.remote.api.UserApi
import com.titanflaws.erp.data.repository.*
import com.titanflaws.erp.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object RepositoryModule {
//
//    @Provides
//    @Singleton
//    fun provideAuthRepository(
//        authApi: AuthApi,
//        firebaseAuth: FirebaseAuth,
//        firestore: FirebaseFirestore
//    ): AuthRepository {
//        return AuthRepositoryImpl(authApi, firebaseAuth, firestore)
//    }
//
//    @Provides
//    @Singleton
//    fun provideUserRepository(
//        userApi: UserApi,
//        userDao: UserDao,
//        firebaseAuth: FirebaseAuth,
//        firestore: FirebaseFirestore
//    ): UserRepository {
//        return UserRepositoryImpl(userApi, userDao, firebaseAuth, firestore)
//    }
//
//    @Provides
//    @Singleton
//    fun provideExamRepository(
//        examApi: ExamApi,
//        examDao: ExamDao
//    ): ExamRepository {
//        return ExamRepositoryImpl(examApi, examDao)
//    }
//
//    @Provides
//    @Singleton
//    fun provideExamResultRepository(
//        examApi: ExamApi,
//        examResultDao: ExamResultDao
//    ): ExamResultRepository {
//        return ExamResultRepositoryImpl(examApi, examResultDao)
//    }
//
//    @Provides
//    @Singleton
//    fun provideStudentRepository(
//        studentDao: StudentDao,
//        firestore: FirebaseFirestore
//    ): StudentRepository {
//        return StudentRepositoryImpl(studentDao, firestore)
//    }
//
//    @Provides
//    @Singleton
//    fun provideTeacherRepository(
//        teacherDao: TeacherDao,
//        firestore: FirebaseFirestore
//    ): TeacherRepository {
//        return TeacherRepositoryImpl(teacherDao, firestore)
//    }
//
//    @Provides
//    @Singleton
//    fun provideFeeRepository(
//        feeDao: FeeDao,
//        firestore: FirebaseFirestore
//    ): FeeRepository {
//        return FeeRepositoryImpl(feeDao, firestore)
//    }
//
//    @Provides
//    @Singleton
//    fun provideAttendanceRepository(
//        attendanceDao: AttendanceDao,
//        firestore: FirebaseFirestore
//    ): AttendanceRepository {
//        return AttendanceRepositoryImpl(attendanceDao, firestore)
//    }
//}