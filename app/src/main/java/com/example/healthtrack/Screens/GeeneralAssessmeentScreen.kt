package com.example.healthtrack.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.healthtrack.ViewModels.GeneralAssessmentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralAssessmentScreen(
    patientId: String,
    vitalId: String,
    viewModel: GeneralAssessmentViewModel,
    navController: NavController
) {
    val visitDate by viewModel.visitDate.collectAsState()
    val generalHealth by viewModel.generalHealth.collectAsState()
    val onDiet by viewModel.onDiet.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var healthExpanded by remember { mutableStateOf(false) }
    var dietExpanded by remember { mutableStateOf(false) }

    val visitDateState = rememberDatePickerState(initialSelectedDateMillis = visitDate?.time)
    val healthOptions = listOf("Good", "Poor")
    val dietOptions = listOf("Yes", "No")

    // Handle success - navigate to patient listing
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            viewModel.clearSuccess()
            navController.navigate("patient_listing") {
                popUpTo(0) // Clear back stack
            }
        }
    }

    // Show error dialog
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        visitDateState.selectedDateMillis?.let { millis ->
                            viewModel.updateVisitDate(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = visitDateState,
                title = {
                    Text(
                        text = "Select Visit Date",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("General Assessment") }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text("Cancel")
                    }

                    val isFormValid = visitDate != null &&
                            generalHealth.isNotBlank() &&
                            onDiet.isNotBlank() &&
                            comments.isNotBlank()

                    Button(
                        onClick = { viewModel.saveAssessment(patientId, vitalId) },
                        enabled = isFormValid && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save Assessment")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "General Health Assessment",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "For patients with BMI ≤ 25",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Visit Date
            OutlinedTextField(
                value = visitDate?.let { dateFormatter.format(it) } ?: "",
                onValueChange = { },
                label = { Text("Visit Date *") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // General Health Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = generalHealth,
                    onValueChange = { },
                    label = { Text("General Health *") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { healthExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select health")
                        }
                    }
                )

                DropdownMenu(
                    expanded = healthExpanded,
                    onDismissRequest = { healthExpanded = false }
                ) {
                    healthOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateGeneralHealth(option)
                                healthExpanded = false
                            }
                        )
                    }
                }
            }

            // On Diet Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = onDiet,
                    onValueChange = { },
                    label = { Text("Have you ever been on a diet to lose weight? *") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { dietExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select diet")
                        }
                    }
                )

                DropdownMenu(
                    expanded = dietExpanded,
                    onDismissRequest = { dietExpanded = false }
                ) {
                    dietOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateOnDiet(option)
                                dietExpanded = false
                            }
                        )
                    }
                }
            }

            // Comments
            OutlinedTextField(
                value = comments,
                onValueChange = viewModel::updateComments,
                label = { Text("Comments *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Enter any additional comments...") },
                singleLine = false,
                maxLines = 4
            )
        }
    }
}