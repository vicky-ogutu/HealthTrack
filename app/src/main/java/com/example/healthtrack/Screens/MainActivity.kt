package com.example.healthtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthtrack.Repositories.AuthRepository
import com.example.healthtrack.Repositories.PatientRegistrationRepository
import com.example.healthtrack.RoomDatabase.PatientDatabase
import com.example.healthtrack.Screens.LoginScreen
import com.example.healthtrack.Screens.PatientRegistrationScreen
//import com.example.healthtrack.Screens.PatientRegistrationScreen
import com.example.healthtrack.Screens.SignUpScreen
import com.example.healthtrack.ViewModels.AuthViewModel
import com.example.healthtrack.ViewModels.PatientRegistrationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HealthTrackApp()
                }
            }
        }
    }
}

@Composable
fun HealthTrackApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Initialize dependencies
    val tokenManager = TokenManager(context)

    // Create AuthRepository and AuthViewModel
    val authRepository = AuthRepository(RetrofitInstance.authApiService)
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository, tokenManager)
    )

    // Create PatientRepository and PatientViewModel
    val patientDatabase = PatientDatabase.getInstance(context)
    val patientRepository = PatientRegistrationRepository(
        patientDao = patientDatabase.patientDao(),
        patientApiService = RetrofitInstance.patientApiService,
        tokenManager = tokenManager
    )
    val patientViewModel: PatientRegistrationViewModel = viewModel(
        factory = PatientRegistrationViewModelFactory(patientRepository)
    )

    NavHost(
        navController = navController,
        startDestination = if (tokenManager.isLoggedIn()) "patient_registration" else "login"
    ) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                navController = navController,
                onNavigateToSignUp = { navController.navigate("signup") },
                onLoginSuccess = { navController.navigate("patient_registration") }
            )
        }

        composable("signup") {
            SignUpScreen(
                authViewModel = authViewModel,
                navController = navController,
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        composable("patient_registration") {
            PatientRegistrationScreen(
                viewModel = patientViewModel,
                navController = navController, // Add this
                onClose = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("patient_registration") { inclusive = true }
                    }
                }
            )
        }
    }
}