package com.example.healthtrack.Screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.healthtrack.ui.theme.HealthTrackTheme



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthtrack.PatientRegistrationScreen
import com.example.healthtrack.auth.TokenManager
import com.example.healthtrack.repository.AuthRepository
import com.example.healthtrack.repository.PatientRepository
import com.example.healthtrack.ui.screens.LoginScreen
import com.example.healthtrack.ui.screens.PatientRegistrationScreen
import com.example.healthtrack.ui.screens.SignUpScreen
import com.example.healthtrack.viewmodel.AuthViewModel
import com.example.healthtrack.viewmodel.PatientViewModel

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
    val authRepository = AuthRepository(RetrofitInstance.authApiService)
    val authViewModel = AuthViewModel(authRepository, tokenManager)

    val patientDatabase = PatientDatabase.getInstance(context)
    val patientRepository = PatientRepository(
        patientDao = patientDatabase.patientDao(),
        patientApiService = RetrofitInstance.patientApiService,
        tokenManager = tokenManager
    )
    val patientViewModel = PatientRegViewModel(patientRepository)

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