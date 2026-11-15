package com.example.sakuin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.sakuin.ui.screens.*

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(
                onFinished = {
                    navController.navigate("login") {
                        popUpTo("splash") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    navController.navigate("profile") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onLoginClick = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate("profile") {
                        popUpTo("register") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onHomeClick = {
                    // Sementara dikosongkan
                },
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo("profile") {
                            inclusive = true
                        }
                    }
                },
                onBackupClick = {
                    navController.navigate("backup")
                },
                onRestoreClick = {
                    navController.navigate("restore")
                }
            )
        }

        composable("backup") {
            BackupScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    // Sementara dikosongkan
                },
                onProfileClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("restore") {
            RestoreScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    // Sementara dikosongkan
                },
                onProfileClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}