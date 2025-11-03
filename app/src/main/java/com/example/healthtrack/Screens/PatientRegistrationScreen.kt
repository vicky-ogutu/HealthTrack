package com.example.healthtrack.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.healthtrack.ViewModels.PatientRegistrationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientRegistrationScreen(
    viewModel: PatientRegistrationViewModel,
    navController: NavController,
    onClose: () -> Unit
) {
    val patientId by viewModel.patientId.collectAsState()
    val registrationDate by viewModel.registrationDate.collectAsState()
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val dateOfBirth by viewModel.dateOfBirth.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val isPatientIdUnique by viewModel.isPatientIdUnique.collectAsState()
    val isFormValid by viewModel.isFormValid.collectAsState()
    val navigateToVitals by viewModel.navigateToVitals.collectAsState() // Now this is Boolean
    val lastSavedPatientId by viewModel.lastSavedPatientId.collectAsState()



    // FIXED: Use the correct types
    LaunchedEffect(navigateToVitals) {
        if (navigateToVitals) { // Check the Boolean flag
            val patientIdToNavigate = lastSavedPatientId ?: patientId // Use lastSavedPatientId or current patientId

            viewModel.clearNavigation()
            viewModel.clearForm()

            println("DEBUG - Navigating to vitals with patientId: $patientIdToNavigate")

            // Ensure patientId is not empty before navigating
            if (patientIdToNavigate.isNotEmpty()) {
                navController.navigate("vitals/$patientIdToNavigate")
            } else {
                println("ERROR - Patient ID is empty, cannot navigate to vitals")
            }
        }
    }
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val scrollState = rememberScrollState()

    // Local state for date pickers
    var showRegistrationDatePicker by remember { mutableStateOf(false) }
    var showDobDatePicker by remember { mutableStateOf(false) }

    val registrationDateState = rememberDatePickerState(
        initialSelectedDateMillis = registrationDate?.time
    )

    val dobDateState = rememberDatePickerState(
        initialSelectedDateMillis = dateOfBirth?.time
    )

    // Registration Date Picker Dialog
    if (showRegistrationDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showRegistrationDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        registrationDateState.selectedDateMillis?.let { millis ->
                            viewModel.updateRegistrationDate(Date(millis))
                        }
                        showRegistrationDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRegistrationDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = registrationDateState,
                title = {
                    Text(
                        text = "Select Registration Date",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    }

    // Date of Birth Picker Dialog
    if (showDobDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDobDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dobDateState.selectedDateMillis?.let { millis ->
                            viewModel.updateDateOfBirth(Date(millis))
                        }
                        showDobDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDobDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = dobDateState,
                title = {
                    Text(
                        text = "Select Date of Birth",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patient Registration") }
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
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text("Close")
                    }

                    Button(
                        onClick = { viewModel.savePatient() },
                        enabled = isFormValid
                    ) {
                        Text("Save Patient")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient ID Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = patientId,
                        onValueChange = viewModel::updatePatientId,
                        label = { Text("Patient ID *") },
                        modifier = Modifier.weight(1f),
                        isError = !isPatientIdUnique && patientId.isNotBlank(),
                        supportingText = {
                            if (!isPatientIdUnique && patientId.isNotBlank()) {
                                Text("Patient ID must be unique")
                            }
                        }
                    )

                    Button(onClick = { viewModel.generatePatientId() }) {
                        Text("Generate")
                    }
                }
            }

            // Registration Date
            OutlinedTextField(
                value = registrationDate?.let { dateFormatter.format(it) } ?: "",
                onValueChange = { },
                label = { Text("Registration Date *") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showRegistrationDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // First Name
            OutlinedTextField(
                value = firstName,
                onValueChange = viewModel::updateFirstName,
                label = { Text("First Name *") },
                modifier = Modifier.fillMaxWidth()
            )

            // Last Name
            OutlinedTextField(
                value = lastName,
                onValueChange = viewModel::updateLastName,
                label = { Text("Last Name *") },
                modifier = Modifier.fillMaxWidth()
            )

            // Date of Birth
            OutlinedTextField(
                value = dateOfBirth?.let { dateFormatter.format(it) } ?: "",
                onValueChange = { },
                label = { Text("Date of Birth *") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDobDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Gender Spinner
            var expanded by remember { mutableStateOf(false) }
            val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = { },
                    label = { Text("Gender *") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select gender")
                        }
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateGender(option)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Form validation summary
            if (!isFormValid && (patientId.isNotBlank() || firstName.isNotBlank() || lastName.isNotBlank())) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = "Please fill all required fields (*) with valid data",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}