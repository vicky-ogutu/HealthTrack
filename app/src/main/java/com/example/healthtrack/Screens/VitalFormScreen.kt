package com.example.healthtrack.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    val isLoading by vitalViewModel.isLoading.collectAsState()
    val savedVital by vitalViewModel.savedVital.collectAsState()

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val visitDateState = rememberDatePickerState(initialSelectedDateMillis = visitDate?.time)

    // Debug: Print form state to console
    LaunchedEffect(visitDate, height, weight, bmi) {
        println("DEBUG - Form State: visitDate=$visitDate, height=$height, weight=$weight, bmi=$bmi")
    }

    // Calculate if form is valid
    val isFormValid = remember(visitDate, height, weight, bmi) {
        val hasVisitDate = visitDate != null
        val hasHeight = height.isNotBlank() && height.toDoubleOrNull() != null && height.toDouble() > 0
        val hasWeight = weight.isNotBlank() && weight.toDoubleOrNull() != null && weight.toDouble() > 0
        val hasBmi = bmi != null

        println("DEBUG - Validation: date=$hasVisitDate, height=$hasHeight, weight=$hasWeight, bmi=$hasBmi")

        hasVisitDate && hasHeight && hasWeight && hasBmi
    }

    // Handle success - navigate based on BMI
    LaunchedEffect(saveSuccess, savedVital) {
        if (saveSuccess && savedVital != null) {
            vitalViewModel.clearSuccess()
            val currentBmi = bmi ?: 0.0

            // Use the server ID if available, otherwise use local ID
            val vitalId = savedVital?.serverId?.toString() ?: savedVital?.id ?: ""

            println("DEBUG - Navigation: patientId=$patientId, BMI=$currentBmi, vitalId=$vitalId")

            // Ensure patientId is not empty
            if (patientId.isNotEmpty()) {
                if (currentBmi <= 25) {
                    // Navigate to General Assessment for BMI ≤ 25
                    navController.navigate("general_assessment/$patientId/$vitalId")
                } else {
                    // Navigate to Overweight Assessment for BMI > 25
                    navController.navigate("overweight_assessment/$patientId/$vitalId")
                }
            } else {
                println("ERROR - Patient ID is empty, cannot navigate to assessment")
                // Fallback: navigate back or show error
                navController.popBackStack()
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
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
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

                    // Debug info in button
                    Button(
                        onClick = {
                            println("DEBUG - Save button clicked")
                            vitalViewModel.saveVital(patientId)
                        },
                        enabled = isFormValid && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Save Vitals")
                                if (!isFormValid) {
                                    Text(
                                        "Check form",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
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
            // Patient ID Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = "Patient ID: $patientId",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

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
                modifier = Modifier.fillMaxWidth(),
                isError = visitDate == null,
                supportingText = {
                    if (visitDate == null) {
                        Text("Visit date is required")
                    }
                }
            )

            // Height
            OutlinedTextField(
                value = height,
                onValueChange = vitalViewModel::updateHeight,
                label = { Text("Height (CM) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Enter height in centimeters") },
                isError = height.isNotBlank() && (height.toDoubleOrNull() == null || height.toDouble() <= 0),
                supportingText = {
                    if (height.isNotBlank()) {
                        when {
                            height.toDoubleOrNull() == null -> Text("Please enter a valid number")
                            height.toDouble() <= 0 -> Text("Height must be greater than 0")
                            else -> Text("Valid height")
                        }
                    }
                }
            )

            // Weight
            OutlinedTextField(
                value = weight,
                onValueChange = vitalViewModel::updateWeight,
                label = { Text("Weight (KG) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Enter weight in kilograms") },
                isError = weight.isNotBlank() && (weight.toDoubleOrNull() == null || weight.toDouble() <= 0),
                supportingText = {
                    if (weight.isNotBlank()) {
                        when {
                            weight.toDoubleOrNull() == null -> Text("Please enter a valid number")
                            weight.toDouble() <= 0 -> Text("Weight must be greater than 0")
                            else -> Text("Valid weight")
                        }
                    }
                }
            )

            // BMI (auto-calculated, read-only)
            OutlinedTextField(
                value = bmi?.toString() ?: "",
                onValueChange = { },
                label = { Text("BMI (Auto-calculated) *") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                singleLine = true,
                placeholder = { Text("BMI will be calculated automatically") },
                isError = bmi == null,
                supportingText = {
                    if (bmi == null) {
                        Text("Enter height and weight to calculate BMI")
                    } else {
                        Text("BMI calculated successfully")
                    }
                }
            )

            // Form Validation Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isFormValid) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isFormValid) "Form is ready to save" else "Form validation issues",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (!isFormValid) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            if (visitDate == null) Text("• Visit date is required")
                            if (height.isBlank() || height.toDoubleOrNull() == null || height.toDouble() <= 0) Text("• Valid height is required")
                            if (weight.isBlank() || weight.toDoubleOrNull() == null || weight.toDouble() <= 0) Text("• Valid weight is required")
                            if (bmi == null) Text("• BMI calculation required")
                        }
                    }
                }
            }

            // BMI Info and Next Step Preview
            if (bmi != null) {
                val bmiStatus = when {
                    bmi!! < 18.5 -> "Underweight"
                    bmi!! <= 25 -> "Normal"
                    else -> "Overweight"
                }

                val nextStep = if (bmi!! <= 25) "General Assessment" else "Overweight Assessment"

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
                            text = "BMI Status: $bmiStatus",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Next Step: $nextStep",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "After saving, you will be directed to the $nextStep form",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}