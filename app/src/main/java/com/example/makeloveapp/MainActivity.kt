package com.example.makeloveapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.makeloveapp.ui.theme.MakeLoveAppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MakeLoveAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DatePickerScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var creationDate by remember { mutableStateOf("") }
    
    // State for direct editing of date components
    var selectedYear by remember { mutableStateOf(selectedDate.year.toString()) }
    var selectedMonth by remember { mutableStateOf(selectedDate.monthValue.toString()) }
    var selectedDay by remember { mutableStateOf(selectedDate.dayOfMonth.toString()) }
    
    // Update individual date components when selectedDate changes
    LaunchedEffect(selectedDate) {
        selectedYear = selectedDate.year.toString()
        selectedMonth = selectedDate.monthValue.toString()
        selectedDay = selectedDate.dayOfMonth.toString()
    }
    
    // Function to update the date from individual components
    fun updateDateFromComponents() {
        try {
            val year = selectedYear.toInt()
            val month = selectedMonth.toInt()
            val day = selectedDay.toInt()
            
            // Validate date components
            if (year in 1900..2100 && month in 1..12 && day in 1..31) {
                try {
                    // Attempt to create a valid date (will throw if invalid like Feb 30)
                    selectedDate = LocalDate.of(year, month, day)
                } catch (e: Exception) {
                    // If invalid date, show error
                    Toast.makeText(context, "Invalid date combination", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: NumberFormatException) {
            // Handle parsing errors
            Toast.makeText(context, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Select a Date",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Display selected date
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        // Direct date editing section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Date Directly",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Month input
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Month", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = selectedMonth,
                            onValueChange = { 
                                if (it.length <= 2 && (it.isEmpty() || it.all { char -> char.isDigit() })) {
                                    selectedMonth = it 
                                    if (it.isNotEmpty()) {
                                        updateDateFromComponents()
                                    }
                                }
                            },
                            modifier = Modifier.width(80.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
                        )
                    }
                    
                    // Day input
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Day", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = selectedDay,
                            onValueChange = { 
                                if (it.length <= 2 && (it.isEmpty() || it.all { char -> char.isDigit() })) {
                                    selectedDay = it 
                                    if (it.isNotEmpty()) {
                                        updateDateFromComponents()
                                    }
                                }
                            },
                            modifier = Modifier.width(80.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
                        )
                    }
                    
                    // Year input
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Year", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = selectedYear,
                            onValueChange = { 
                                if (it.length <= 4 && (it.isEmpty() || it.all { char -> char.isDigit() })) {
                                    selectedYear = it 
                                    if (it.length == 4) {
                                        updateDateFromComponents()
                                    }
                                }
                            },
                            modifier = Modifier.width(100.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
                        )
                    }
                }
            }
        }
        
        // Button to open date picker
        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Use Calendar Picker")
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Submit button
        Button(
            onClick = {
                isLoading = true
                // Simulate waiting for result
                Toast.makeText(context, "Calculating creation date...", Toast.LENGTH_SHORT).show()
                
                // Calculate the estimated creation date based on the selected date
                calculateCreationDate(selectedDate) { result ->
                    creationDate = result
                    isLoading = false
                    showResult = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Calculate Creation Date")
            }
        }
        
        // Show result
        AnimatedVisibility(
            visible = showResult,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Estimated Creation Date",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = creationDate,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    
    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            // Convert milliseconds to LocalDate
                            val instant = java.time.Instant.ofEpochMilli(millis)
                            val zoneId = java.time.ZoneId.systemDefault()
                            selectedDate = instant.atZone(zoneId).toLocalDate()
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
            DatePicker(state = datePickerState)
        }
    }
}

// Function to calculate the estimated creation date based on the selected date
private fun calculateCreationDate(selectedDate: LocalDate, callback: (String) -> Unit) {
    // Simulate a calculation delay
    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
        // Calculate conception date (approximately 9 months or ~266 days before birth)
        val conceptionDate = selectedDate.minus(266, ChronoUnit.DAYS)
        
        // Add some randomness to make it more fun (plus or minus 14 days)
        val randomDays = Random.nextInt(-14, 15)
        val estimatedCreationDate = conceptionDate.plus(randomDays.toLong(), ChronoUnit.DAYS)
        
        // Format the date nicely
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
        val formattedDate = estimatedCreationDate.format(formatter)
        
        // Add a fun message
        val dayOfWeek = estimatedCreationDate.dayOfWeek.toString().lowercase().replaceFirstChar { it.uppercase() }
        val timeOfDay = when (Random.nextInt(0, 4)) {
            0 -> "evening"
            1 -> "night"
            2 -> "afternoon"
            else -> "morning"
        }
        
        val mood = when (Random.nextInt(0, 5)) {
            0 -> "romantic"
            1 -> "passionate"
            2 -> "spontaneous"
            3 -> "loving"
            else -> "magical"
        }
        
        val result = "You were likely conceived on $formattedDate, " +
                "which was a $dayOfWeek $timeOfDay. " +
                "It was probably a $mood moment! 💕"
        
        callback(result)
    }, 2000) // 2 second delay for effect
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun DatePickerScreenPreview() {
    MakeLoveAppTheme {
        DatePickerScreen()
    }
}