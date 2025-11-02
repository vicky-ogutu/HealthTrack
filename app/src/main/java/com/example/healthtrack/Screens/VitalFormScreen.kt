package com.example.healthtrack.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.healthtrack.ViewModels.VitalViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsFormScreen(
    patientId: String,
    vitalViewModel: VitalViewModel,
    navController: NavController
) {
    val visitDate by vitalViewModel.visitDate.collectAsState()
    val height by vitalViewModel.height.collectAsState()
    val weight by vitalViewModel.weight.collectAsState()
    val bmi by vitalViewModel.bmi.collectAsState()
    val saveSuccess by vitalViewModel.saveSuccess.collectAsState()
    val errorMessage by vitalViewModel.errorMessage.collectAsState()

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val visitDateState = rememberDatePickerState(initialSelectedDateMillis = visitDate?.time)

    // Handle success - navigate based on BMI
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            vitalViewModel.clearSuccess()
            val currentBmi = bmi ?: 0.0
            if (currentBmi <= 25) {
                navController.navigate("general_assessment/$patientId")
            } else {
                navController.navigate("overweight_assessment/$patientId")
            }
        }
    }

    // Show error dialog
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { vitalViewModel.clearError() },
            title = { Text("Error") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                TextButton(onClick = { vitalViewModel.clearError() }) {
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
                            vitalViewModel.updateVisitDate(Date(millis))
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
                title = { Text("Patient Vitals") }
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
                            height.isNotBlank() &&
                            weight.isNotBlank() &&
                            bmi != null

                    Button(
                        onClick = { vitalViewModel.saveVital(patientId) },
                        enabled = isFormValid
                    ) {
                        Text("Save Vitals")
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

            // Height
            OutlinedTextField(
                value = height,
                onValueChange = vitalViewModel::updateHeight,
                label = { Text("Height (CM) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Enter height in centimeters") }
            )

            // Weight
            OutlinedTextField(
                value = weight,
                onValueChange = vitalViewModel::updateWeight,
                label = { Text("Weight (KG) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Enter weight in kilograms") }
            )

            // BMI (auto-calculated, read-only)
            OutlinedTextField(
                value = bmi?.toString() ?: "",
                onValueChange = { },
                label = { Text("BMI (Auto-calculated)") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                singleLine = true,
                placeholder = { Text("BMI will be calculated automatically") }
            )

            // BMI Info
            if (bmi != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            bmi!! < 18.5 -> MaterialTheme.colorScheme.primaryContainer
                            bmi!! <= 25 -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.tertiaryContainer
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "BMI Status: ${
                                when {
                                    bmi!! < 18.5 -> "Underweight"
                                    bmi!! <= 25 -> "Normal"
                                    else -> "Overweight"
                                }
                            }",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Next: ${
                                if (bmi!! <= 25) "General Assessment" else "Overweight Assessment"
                            }",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}