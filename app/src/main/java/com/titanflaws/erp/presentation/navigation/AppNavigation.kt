package com.titanflaws.erp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.titanflaws.erp.presentation.screens.admin.AdminDashboardScreen
import com.titanflaws.erp.presentation.screens.admin.ManageUsersScreen
import com.titanflaws.erp.presentation.screens.admin.ManageClassesScreen
import com.titanflaws.erp.presentation.screens.admin.ManageCoursesScreen
import com.titanflaws.erp.presentation.screens.parent.ParentDashboardScreen
import com.titanflaws.erp.presentation.screens.staff.StaffDashboardScreen
import com.titanflaws.erp.presentation.screens.student.StudentDashboardScreen
import com.titanflaws.erp.presentation.screens.teacher.TeacherDashboardScreen
import com.titanflaws.erp.presentation.screens.common.NotificationsScreen
import com.titanflaws.erp.presentation.screens.common.ProfileScreen
import com.titanflaws.erp.presentation.screens.common.SettingsScreen
import com.titanflaws.erp.presentation.viewmodel.AuthViewModel
import com.titanflaws.erp.presentation.viewmodel.UserRole
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Route constants for the app navigation
 */
object Routes {
    // Admin routes
    const val ADMIN = "admin"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val MANAGE_USERS = "manage_users"
    const val MANAGE_CLASSES = "manage_classes"
    const val MANAGE_COURSES = "manage_courses"
    const val SCHOOL_SETTINGS = "school_settings"
    
    // Teacher routes
    const val TEACHER = "teacher"
    const val TEACHER_DASHBOARD = "teacher_dashboard"
    const val TEACHER_ATTENDANCE = "teacher_attendance"
    const val TEACHER_EXAMS = "teacher_exams"
    const val TEACHER_CREATE_EXAM = "teacher_create_exam"
    const val TEACHER_COURSES = "teacher_courses"
    const val TEACHER_ASSIGNMENTS = "teacher_assignments"
    
    // Student routes
    const val STUDENT = "student"
    const val STUDENT_DASHBOARD = "student_dashboard"
    const val STUDENT_ATTENDANCE = "student_attendance"
    const val EXAM_RESULT = "exam_result"
    const val STUDENT_TIMETABLE = "student_timetable"
    const val STUDENT_COURSES = "student_courses"
    const val STUDENT_PROFILE = "student_profile"
    
    // Parent routes
    const val PARENT = "parent"
    const val PARENT_DASHBOARD = "parent_dashboard"
    const val STUDENT_PROGRESS = "student_progress"
    const val STUDENT_FEES = "student_fees"
    const val CHILD_ATTENDANCE = "child_attendance"
    
    // Staff routes
    const val STAFF = "staff"
    const val STAFF_DASHBOARD = "staff_dashboard"
    const val MANAGE_TRANSPORT = "manage_transport"
    const val MANAGE_INVENTORY = "manage_inventory"
    const val MANAGE_HOSTEL = "manage_hostel"
    
    // Common routes
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val NOTIFICATIONS = "notifications"
}

/**
 * Main navigation component for the app
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AuthRoutes.AUTH_GRAPH,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication graph using the new AuthNavigation
        authNavigation(
            navController = navController,
            onNavigateToHome = {
                // Navigate based on user role
                when (authViewModel.authState.value.userRole) {
                    is UserRole.Admin -> navController.navigate(Routes.ADMIN) {
                        popUpTo(AuthRoutes.AUTH_GRAPH) { inclusive = true }
                    }
                    is UserRole.Teacher -> navController.navigate(Routes.TEACHER) {
                        popUpTo(AuthRoutes.AUTH_GRAPH) { inclusive = true }
                    }
                    is UserRole.Student -> navController.navigate(Routes.STUDENT) {
                        popUpTo(AuthRoutes.AUTH_GRAPH) { inclusive = true }
                    }
                    is UserRole.Parent -> navController.navigate(Routes.PARENT) {
                        popUpTo(AuthRoutes.AUTH_GRAPH) { inclusive = true }
                    }
                    is UserRole.Staff -> navController.navigate(Routes.STAFF) {
                        popUpTo(AuthRoutes.AUTH_GRAPH) { inclusive = true }
                    }
                    is UserRole.Unknown -> navController.navigate(Routes.STUDENT) {
                        // Default to student dashboard
                        popUpTo(AuthRoutes.AUTH_GRAPH) { inclusive = true }
                    }
                }
            }
        )
        
        // Admin graph 
        adminGraph(navController, authViewModel)
        
        // Teacher graph
        teacherGraph(navController, authViewModel)
        
        // Student graph
        studentGraph(navController, authViewModel)
        
        // Parent graph
        parentGraph(navController, authViewModel)
        
        // Staff graph
        staffGraph(navController, authViewModel)
        
        // Common routes
        commonRoutes(navController, authViewModel)
    }
}

/**
 * Admin navigation graph
 */
fun NavGraphBuilder.adminGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(startDestination = Routes.ADMIN_DASHBOARD, route = Routes.ADMIN) {
        composable(Routes.ADMIN_DASHBOARD) {
            AdminDashboardScreen(
                onNavigateToUsers = {
                    navController.navigate(Routes.MANAGE_USERS)
                },
                onNavigateToClasses = {
                    navController.navigate(Routes.MANAGE_CLASSES)
                },
                onNavigateToCourses = {
                    navController.navigate(Routes.MANAGE_COURSES)
                },
                onNavigateToTeachers = {
                },
                onNavigateToStudents = {
                },
                onNavigateToAttendance = {
                },
                onNavigateToExams = {},
                onNavigateToTimetables = {},
                onNavigateToAnnouncements = {},
                onNavigateToSettings = {
                    navController.navigate(Routes.SCHOOL_SETTINGS)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
//                onSignOut = {
//                    authViewModel.signOut()
//                    navController.navigateToAuth()
//                }
            )
        }
        
        composable(Routes.MANAGE_USERS) {
            ManageUsersScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Routes.MANAGE_CLASSES) {
            ManageClassesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Routes.MANAGE_COURSES) {
            ManageCoursesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Add more routes as needed
    }
}

/**
 * Teacher navigation graph
 */
fun NavGraphBuilder.teacherGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(startDestination = Routes.TEACHER_DASHBOARD, route = Routes.TEACHER) {
        composable(Routes.TEACHER_DASHBOARD) {
            TeacherDashboardScreen(
                onNavigateToAttendance = {
                    navController.navigate(Routes.TEACHER_ATTENDANCE)
                },
                onNavigateToExams = {
                    navController.navigate(Routes.TEACHER_EXAMS)
                },
                onNavigateToCourses = {
                    navController.navigate(Routes.TEACHER_COURSES)
                },
                onNavigateToAssignments = {
                    navController.navigate(Routes.TEACHER_ASSIGNMENTS)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onNavigateToNotifications = {
                    navController.navigate(Routes.NOTIFICATIONS)
                },
                onNavigateToCreateExam = {  },
                onNavigateToStudents = {  },
                onNavigateToTimetable = {  },
//                onSignOut = {
//                    authViewModel.signOut()
//                    navController.navigateToAuth()
//                }
            )
        }
        
        // Add more routes as needed
    }
}

/**
 * Student navigation graph
 */
fun NavGraphBuilder.studentGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(startDestination = Routes.STUDENT_DASHBOARD, route = Routes.STUDENT) {
        composable(Routes.STUDENT_DASHBOARD) {
            StudentDashboardScreen(
                onNavigateToAttendance = {
                    navController.navigate(Routes.STUDENT_ATTENDANCE)
                },
                onNavigateToExams = {
                    navController.navigate(Routes.EXAM_RESULT)
                },
                onNavigateToTimetable = {
                    navController.navigate(Routes.STUDENT_TIMETABLE)
                },
                onNavigateToCourses = {
                    navController.navigate(Routes.STUDENT_COURSES)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onNavigateToNotifications = {
                    navController.navigate(Routes.NOTIFICATIONS)
                },
                onNavigateToAssignments = {  },
//                onSignOut = {
//                    authViewModel.signOut()
//                    navController.navigateToAuth()
//                }
            )
        }
        
        // Add more routes as needed
    }
}

/**
 * Parent navigation graph
 */
fun NavGraphBuilder.parentGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(startDestination = Routes.PARENT_DASHBOARD, route = Routes.PARENT) {
        composable(Routes.PARENT_DASHBOARD) {
            ParentDashboardScreen(
                onNavigateToChildAttendance = { studentId ->
                    navController.navigate("${Routes.CHILD_ATTENDANCE}/$studentId")
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onNavigateToNotifications = {
                    navController.navigate(Routes.NOTIFICATIONS)
                },
                onNavigateToFeePayment = {},
                onNavigateToChildProgress = {}
//                onSignOut = {
//                    authViewModel.signOut()
//                    navController.navigateToAuth()
//                }
            )
        }
        
        // Add more routes as needed
    }
}

/**
 * Staff navigation graph
 */
fun NavGraphBuilder.staffGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    navigation(startDestination = Routes.STAFF_DASHBOARD, route = Routes.STAFF) {
        composable(Routes.STAFF_DASHBOARD) {
            StaffDashboardScreen(
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onNavigateToNotifications = {
                    navController.navigate(Routes.NOTIFICATIONS)
                },
                onNavigateToManageTransport = {  },
                onNavigateToManageHostel = {},
                onNavigateToManageInventory = {},
            )
        }
        
        // Add more routes as needed
    }
}

/**
 * Common routes used across different user roles
 */
fun NavGraphBuilder.commonRoutes(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    composable(Routes.PROFILE) {
        val userId = it.arguments?.getString("userId")
        ProfileScreen(
            onNavigateBack = {
                navController.popBackStack()
            },
            userId = userId ?: "not found"
        )
    }
    
    composable(Routes.SETTINGS) {
        SettingsScreen(
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
    
    composable(Routes.NOTIFICATIONS) {
        NotificationsScreen(
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToDetails = { _, _ ->

            }
        )
    }
} 