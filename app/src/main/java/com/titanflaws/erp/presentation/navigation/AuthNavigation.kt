package com.titanflaws.erp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.titanflaws.erp.presentation.screens.auth.ForgotPasswordScreen
import com.titanflaws.erp.presentation.screens.auth.LoginScreen
import com.titanflaws.erp.presentation.screens.auth.PhoneLoginScreen
import com.titanflaws.erp.presentation.screens.auth.RegisterScreen

/**
 * Authentication-related route constants
 */
object AuthRoutes {
    const val AUTH_GRAPH = "auth_graph"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val PHONE_LOGIN = "phone_login"
}

/**
 * Authentication navigation graph
 */
fun NavGraphBuilder.authNavigation(
    navController: NavHostController,
    onNavigateToHome: () -> Unit
) {
    navigation(
        startDestination = AuthRoutes.LOGIN,
        route = AuthRoutes.AUTH_GRAPH
    ) {
        // Login Screen
        composable(route = AuthRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(AuthRoutes.REGISTER)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(AuthRoutes.FORGOT_PASSWORD)
                },
                onNavigateToHome = onNavigateToHome,
                onNavigateToPhoneLogin = {
                    navController.navigate(AuthRoutes.PHONE_LOGIN)
                }
            )
        }
        
        // Register Screen
        composable(route = AuthRoutes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = onNavigateToHome
            )
        }
        
        // Forgot Password Screen
        composable(route = AuthRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        // Phone Login Screen
        composable(route = AuthRoutes.PHONE_LOGIN) {
            PhoneLoginScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = onNavigateToHome
            )
        }
    }
}

/**
 * Extension function to navigate to the authentication graph
 */
fun NavHostController.navigateToAuth() {
    this.navigate(AuthRoutes.AUTH_GRAPH) {
        popUpTo(this@navigateToAuth.graph.id) {
            inclusive = true
        }
    }
} 