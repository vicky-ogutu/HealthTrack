package com.example.healthtrack

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import com.example.healthtrack.Repositories.*
import com.example.healthtrack.RoomDatabase.PatientDatabase
import com.example.healthtrack.Screens.*
import com.example.healthtrack.ViewModels.*

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HealthTrackApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val tokenManager = TokenManager(context)

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

    val visitRepository = VisitRepository(
        visitApiService = RetrofitInstance.visitApiService,
        tokenManager = tokenManager
    )

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(authRepository, tokenManager)
    )

    val patientViewModel: PatientRegistrationViewModel = viewModel(
        factory = PatientRegistrationViewModel.Factory(patientRepository)
    )

    val vitalViewModel: VitalViewModel = viewModel(
        factory = VitalViewModel.Factory(vitalRepository)
    )

    val generalAssessmentViewModel: GeneralAssessmentViewModel = viewModel(
        factory = GeneralAssessmentViewModel.Factory(visitRepository)
    )

    val overweightAssessmentViewModel: OverweightAssessmentViewModel = viewModel(
        factory = OverweightAssessmentViewModel.Factory(visitRepository)
    )

    NavHost(
        navController = navController,
        startDestination = if (tokenManager.isLoggedIn()) "patient_listing" else "login"
    ) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("patient_listing") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate("signup") },
                navController = TODO()
            )
        }

        composable("signup") {
            SignUpScreen(
                authViewModel = authViewModel,
                navController = navController,
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        composable("patient_listing") {
            val patientListViewModel: PatientListViewModel = viewModel(
                factory = PatientListViewModel.Factory(tokenManager)
            )
            PatientListScreen(
                viewModel = patientListViewModel,
                onAddPatientClick = { navController.navigate("patient_registration") },
                onPatientClick = { patientId ->
                    navController.navigate("vitals/$patientId")
                }
            )
        }

        composable("patient_registration") {
            PatientRegistrationScreen(
                viewModel = patientViewModel,
                navController = navController,
                onClose = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("patient_listing") { inclusive = true }
                    }
                }
            )
        }

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

        composable(
            "general_assessment/{patientId}/{vitalId}",
            arguments = listOf(
                navArgument("patientId") { type = NavType.StringType },
                navArgument("vitalId") { type = NavType.StringType }
            )
        ) { entry ->
            GeneralAssessmentScreen(
                patientId = entry.arguments?.getString("patientId") ?: "",
                vitalId = entry.arguments?.getString("vitalId") ?: "",
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
        ) { entry ->
            OverweightAssessmentScreen(
                patientId = entry.arguments?.getString("patientId") ?: "",
                vitalId = entry.arguments?.getString("vitalId") ?: "",
                viewModel = overweightAssessmentViewModel,
                navController = navController
            )
        }
    }
}
