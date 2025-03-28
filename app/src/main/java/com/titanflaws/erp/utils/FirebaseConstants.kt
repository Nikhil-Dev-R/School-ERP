package com.titanflaws.erp.utils

/**
 * Constants for Firebase collection and document names
 */
object FirebaseConstants {
    // Collection names
    const val COLLECTION_USERS = "users"
    const val COLLECTION_TEACHERS = "teachers"
    const val COLLECTION_STUDENTS = "students"
    const val COLLECTION_COURSES = "courses"
    const val COLLECTION_CLASS_SECTIONS = "class_sections"
    const val COLLECTION_ATTENDANCE = "attendance"
    const val COLLECTION_EXAMS = "exams"
    const val COLLECTION_EXAM_RESULTS = "exam_results"
    const val COLLECTION_FEES = "fees"
    const val COLLECTION_FEE_PAYMENTS = "fee_payments"
    const val COLLECTION_PARENTS = "parents"
    const val COLLECTION_STAFF = "staff"
    const val COLLECTION_NOTICES = "notices"
    const val COLLECTION_TIMETABLE = "timetable"
    const val COLLECTION_EVENTS = "events"
    const val COLLECTION_ASSIGNMENTS = "assignments"
    const val COLLECTION_SUBMISSIONS = "submissions"
    const val COLLECTION_TRANSPORT = "transport"
    const val COLLECTION_INVENTORY = "inventory"
    const val COLLECTION_HOSTEL = "hostel"
    const val COLLECTION_ACADEMIC_YEARS = "academic_years"
    
    // Document field names
    const val FIELD_ID = "id"
    const val FIELD_UID = "uid"
    const val FIELD_EMAIL = "email"
    const val FIELD_FULL_NAME = "fullName"
    const val FIELD_ROLE = "role"
    const val FIELD_ACTIVE = "isActive"
    const val FIELD_CREATED_AT = "createdAt"
    const val FIELD_UPDATED_AT = "updatedAt"
    
    // Role values
    const val ROLE_ADMIN = "Admin"
    const val ROLE_TEACHER = "Teacher"
    const val ROLE_STUDENT = "Student"
    const val ROLE_PARENT = "Parent"
    const val ROLE_STAFF = "Staff"
} 