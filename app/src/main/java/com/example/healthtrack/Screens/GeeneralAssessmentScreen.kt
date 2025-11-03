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
    val onDrugs by viewModel.onDrugs.collectAsState() // ✅ added
    val comments by viewModel.comments.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var healthExpanded by remember { mutableStateOf(false) }
    var dietExpanded by remember { mutableStateOf(false) }
    var drugsExpanded by remember { mutableStateOf(false) } // ✅ added

    val visitDateState = rememberDatePickerState(initialSelectedDateMillis = visitDate?.time)
    val healthOptions = listOf("Good", "Poor")
    val dietOptions = listOf("Yes", "No")
    val drugsOptions = listOf("Yes", "No") // ✅ added

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            viewModel.clearSuccess()
            navController.navigate("patient_listing") { popUpTo(0) }
        }
    }

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
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(
                state = visitDateState,
                title = { Text("Select Visit Date", Modifier.padding(16.dp)) }
            )
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("General Assessment") }) },
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
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) { Text("Cancel") }

                    val isFormValid = visitDate != null &&
                            generalHealth.isNotBlank() &&
                            onDiet.isNotBlank() &&
                            onDrugs.isNotBlank() && // ✅ must not be blank
                            comments.isNotBlank()

                    Button(
                        onClick = { viewModel.saveAssessment(patientId, vitalId) },
                        enabled = isFormValid && !isLoading
                    ) {
                        if (isLoading)
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        else
                            Text("Save Assessment")
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
            Text("General Health Assessment", style = MaterialTheme.typography.headlineSmall)
            Text(
                "For patients with BMI ≤ 25",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = visitDate?.let { dateFormatter.format(it) } ?: "",
                onValueChange = { },
                label = { Text("Visit Date *") },
                readOnly = true,
                trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.DateRange, null) } },
                modifier = Modifier.fillMaxWidth()
            )

            // General Health
            DropdownField(
                label = "General Health *",
                value = generalHealth,
                expanded = healthExpanded,
                onExpandChange = { healthExpanded = it },
                options = healthOptions,
                onSelect = { viewModel.updateGeneralHealth(it) }
            )

            // On Diet
            DropdownField(
                label = "Have you ever been on a diet? *",
                value = onDiet,
                expanded = dietExpanded,
                onExpandChange = { dietExpanded = it },
                options = dietOptions,
                onSelect = { viewModel.updateOnDiet(it) }
            )

            // ✅ On Drugs
            DropdownField(
                label = "Are you currently on any drugs? *",
                value = onDrugs,
                expanded = drugsExpanded,
                onExpandChange = { drugsExpanded = it },
                options = drugsOptions,
                onSelect = { viewModel.updateOnDrugs(it) }
            )

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

@Composable
private fun DropdownField(
    label: String,
    value: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { onExpandChange(true) }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpandChange(false) }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelect(option)
                    onExpandChange(false)
                })
            }
        }
    }
}
