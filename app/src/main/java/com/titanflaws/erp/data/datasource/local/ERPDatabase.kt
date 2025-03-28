package com.titanflaws.erp.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.titanflaws.erp.data.datasource.local.converters.DateConverter
import com.titanflaws.erp.data.datasource.local.converters.ListConverter
import com.titanflaws.erp.data.datasource.local.dao.*
import com.titanflaws.erp.data.model.*

@Database(
    entities = [
        User::class,
        Student::class,
        Teacher::class,
        Staff::class,
        Parent::class,
        Course::class,
        ClassSection::class,
        Attendance::class,
        Exam::class,
        ExamResult::class,
        Fee::class,
        FeePayment::class,
        Assignment::class,
        Notification::class,
        TimeTable::class,
        Library::class,
        BookIssue::class,
        Transport::class,
        RouteStop::class,
        Hostel::class,
        HostelRoom::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class ERPDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao
//    abstract fun staffDao(): StaffDao
//    abstract fun parentDao(): ParentDao
    abstract fun courseDao(): CourseDao
    abstract fun classSectionDao(): ClassSectionDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun examDao(): ExamDao
    abstract fun examResultDao(): ExamResultDao
    abstract fun feeDao(): FeeDao
    abstract fun feePaymentDao(): FeePaymentDao
//    abstract fun assignmentDao(): AssignmentDao
//    abstract fun notificationDao(): NotificationDao
//    abstract fun timeTableDao(): TimeTableDao
//    abstract fun libraryDao(): LibraryDao
//    abstract fun bookIssueDao(): BookIssueDao
//    abstract fun transportDao(): TransportDao
//    abstract fun routeStopDao(): RouteStopDao
//    abstract fun hostelDao(): HostelDao
//    abstract fun hostelRoomDao(): HostelRoomDao
} 