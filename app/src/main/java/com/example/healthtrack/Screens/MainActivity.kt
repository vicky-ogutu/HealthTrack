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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.healthtrack.Repositories.AuthRepository
import com.example.healthtrack.Repositories.PatientRegistrationRepository
import com.example.healthtrack.Repositories.VisitRepository
import com.example.healthtrack.Repositories.VitalRepository
import com.example.healthtrack.RoomDatabase.PatientDatabase
import com.example.healthtrack.Screens.LoginScreen
import com.example.healthtrack.Screens.PatientRegistrationScreen
import com.example.healthtrack.Screens.SignUpScreen
import com.example.healthtrack.Screens.VitalsFormScreen
import com.example.healthtrack.ViewModels.AuthViewModel
import com.example.healthtrack.ViewModels.PatientRegistrationViewModel
import com.example.healthtrack.ViewModels.VitalViewModel

import com.example.healthtrack.Screens.GeneralAssessmentScreen
import com.example.healthtrack.Screens.OverweightAssessmentScreen
import com.example.healthtrack.ViewModels.GeneralAssessmentViewModel
import com.example.healthtrack.ViewModels.OverweightAssessmentViewModel

import androidx.navigation.navArgument
import com.example.healthtrack.Screens.PatientListingScreen

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

    // Create repositories
    val authRepository = AuthRepository(RetrofitInstance.authApiService)
    val patientDatabase = PatientDatabase.getInstance(context)

    val patientRepository = PatientRegistrationRepository(
        patientDao = patientDatabase.patientDao(),
        patientApiService = RetrofitInstance.patientApiService,
        tokenManager = tokenManager
    )

    val vitalRepository = VitalRepository(
        vitalDao = patientDatabase.vitalDao(),
        vitalApiService = RetrofitInstance.vitalApiService,
        tokenManager = tokenManager
    )

    // Create ViewModels
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(authRepository, tokenManager)
    )

    val patientViewModel: PatientRegistrationViewModel = viewModel(
        factory = PatientRegistrationViewModel.Factory(patientRepository)
    )

    val vitalViewModel: VitalViewModel = viewModel(
        factory = VitalViewModel.Factory(vitalRepository)
    )

    val visitRepository = VisitRepository(
        visitApiService = RetrofitInstance.visitApiService,
        tokenManager = tokenManager
    )

    val generalAssessmentViewModel: GeneralAssessmentViewModel = viewModel(
        factory = GeneralAssessmentViewModel.Factory(visitRepository)
    )

    val overweightAssessmentViewModel: OverweightAssessmentViewModel = viewModel(
        factory = OverweightAssessmentViewModel.Factory(visitRepository)
    )

    // Update the NavHost in MainActivity:
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
                navController = navController,
                onClose = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("patient_registration") { inclusive = true }
                    }
                }
            )
        }

        // FIXED: Add patientId parameter to vitals route
        composable(
            "vitals/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            VitalsFormScreen(
                patientId = patientId,
                vitalViewModel = vitalViewModel,
                navController = navController
            )
        }

        // FIXED: Add proper arguments for assessment routes
        composable(
            "general_assessment/{patientId}/{vitalId}",
            arguments = listOf(
                navArgument("patientId") { type = NavType.StringType },
                navArgument("vitalId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            val vitalId = backStackEntry.arguments?.getString("vitalId") ?: ""
            GeneralAssessmentScreen(
                patientId = patientId,
                vitalId = vitalId,
                viewModel = generalAssessmentViewModel,
                navController = navController
            )
        }

        composable(
            "overweight_assessment/{patientId}/{vitalId}",
            arguments = listOf(
                navArgument("patientId") { type = NavType.StringType },
                navArgument("vitalId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            val vitalId = backStackEntry.arguments?.getString("vitalId") ?: ""
            OverweightAssessmentScreen(
                patientId = patientId,
                vitalId = vitalId,
                viewModel = overweightAssessmentViewModel,
                navController = navController
            )
        }
        composable("patient_listing") {
            PatientListingScreen(
                //navController = navController
            )
        }

        composable("patient_registration") {
            PatientRegistrationScreen(
                viewModel = patientViewModel,
                navController = navController,
                onClose = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("patient_registration") { inclusive = true }
                    }
                }
            )
        }

        // FIXED: Use simpler route pattern without arguments first
        composable("vitals") {
            VitalsFormScreen(
                patientId = "", // We'll pass this through ViewModel state
                vitalViewModel = vitalViewModel,
                navController = navController
            )
        }
    }
}