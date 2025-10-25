package com.fit2081.nutritrack

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.fit2081.nutritrack.data.AuthManager
import com.fit2081.nutritrack.data.foodintakes.FoodIntake
import com.fit2081.nutritrack.data.foodintakes.FoodIntakeViewModel
import com.fit2081.nutritrack.data.patients.PatientViewModel
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class FoodQuestionnaire : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                val viewModel: FoodIntakeViewModel = ViewModelProvider(
                    this, FoodIntakeViewModel.FoodIntakeViewModelFactory(this)
                )[FoodIntakeViewModel::class.java]

                QuestionnaireScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(viewModel: FoodIntakeViewModel) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    // Context
    val context = LocalContext.current
    val userId = AuthManager.getUserId()

    // Food categories
    val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood",
        "Poultry", "Fish", "Eggs", "Nuts/Seeds")
    var selectedCategories = remember { mutableStateListOf<String>() }

    // Persona
    var expanded by remember { mutableStateOf(false) }
    var selectedPersona by remember { mutableStateOf<Persona?>(null) }
    var selectedOption by remember { mutableStateOf("") }

    // Timings
    var biggestMealTime = remember { mutableStateOf("00:00") }
    var sleepTime = remember { mutableStateOf("00:00") }
    var wakeUpTime = remember { mutableStateOf("00:00") }
    val mealTimeError = remember { mutableStateOf<String?>(null) }
    val sleepTimeError = remember { mutableStateOf<String?>(null) }
    val wakeUpTimeError = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        // Launch this effect whenever the userId changes
        val intake = viewModel.getFoodIntakeByUserId(userId.toString())
        if (intake != null) {
            // Restore the selected food categories from comma-separated string
            selectedCategories.clear()
            selectedCategories.addAll(intake.selectedCategories.split(","))
            // Find and select the previously chosen persona
            selectedPersona = personas.find { it.name == intake.selectedPersona }
            // Set the selected option based on the persona
            selectedOption = selectedPersona?.name ?: ""
            // Set the selected timings
            biggestMealTime.value = intake.biggestMealTime
            sleepTime.value = intake.sleepTime
            wakeUpTime.value = intake.wakeUpTime
        }
    }

    Scaffold(
        // Referenced from W2Lab4
        topBar ={
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFa8c7af),
                    titleContentColor = Color(0xFF1b5a45)
                ),
                title = {
                    Text(
                        "Food Intake Questionnaire",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBackPressedDispatcher?.onBackPressed()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header for Food Categories
            Text(
                text = "Tick all food categories you can eat",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            // Food Categories Checkboxes
            CheckboxQuestionnaire(foodCategories, selectedCategories)

            Spacer(modifier = Modifier.height(16.dp))

            // Persona Header
            Text(
                text = "Your Persona",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "People can be broadly classified into 6 different types based on " +
                        "their eating preferences. Click on each button below to find out " +
                        "the different types, and select the type that best fits you!",
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))

            // Persona Modal Pop-up
            ShowButtonAndModal()
            Spacer(modifier = Modifier.height(16.dp))

            // Persona Selection Header
            Text(
                text = "Which persona best fits you?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))

            // Persona Dropdown
            // Referenced from LoginScreen
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = true },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                TextField(
                    value = selectedOption,
                    onValueChange = {},
                    label = { Text("Select option") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    // Referenced from ChatGPT
                    personas.forEach { persona ->
                        DropdownMenuItem(
                            text = { Text(persona.name) },
                            onClick = {
                                selectedPersona = persona
                                selectedOption = persona.name
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Timings Section Header
            Text(
                text = "Timings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            // Time Pickers
            TimePickerRow("What time of day approx. do you normally eat your biggest meal?", biggestMealTime, mealTimeError.value)
            TimePickerRow("What time of day approx. do you go to sleep at night?", sleepTime, sleepTimeError.value)
            TimePickerRow("What time of day approx. do you wake up in the morning?", wakeUpTime, wakeUpTimeError.value)

            Spacer(modifier = Modifier.height(15.dp))

            // Save Button Section
            Button(onClick = {
                if (selectedCategories.isEmpty()) {
                    Toast.makeText(context, "Please select at least one food category you can eat.",
                        Toast.LENGTH_LONG).show()
                    return@Button
                }
                if (selectedPersona == null) {
                    Toast.makeText(context, "Please select a persona.", Toast.LENGTH_LONG).show()
                    return@Button
                }

                if (biggestMealTime.value == "00:00" || sleepTime.value == "00:00" || wakeUpTime.value == "00:00") {
                    Toast.makeText(context, "Please complete all timing fields.", Toast.LENGTH_LONG).show()
                    return@Button
                }

                val times = listOf(biggestMealTime.value, sleepTime.value, wakeUpTime.value)
                // Check if any of the three time inputs are the same
                if (times.distinct().size != 3) {
                    if (biggestMealTime.value == sleepTime.value || biggestMealTime.value == wakeUpTime.value)
                        mealTimeError.value = "Meal time must be different from sleep and wake-up times"
                    if (sleepTime.value == biggestMealTime.value || sleepTime.value == wakeUpTime.value)
                        sleepTimeError.value = "Sleep time must be different from meal and wake-up times"
                    if (wakeUpTime.value == biggestMealTime.value || wakeUpTime.value == sleepTime.value)
                        wakeUpTimeError.value = "Wake-up time must be different from meal and sleep times"
                    return@Button
                }

                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val wake = LocalTime.parse(wakeUpTime.value, formatter)
                val meal = LocalTime.parse(biggestMealTime.value, formatter)
                val sleep = LocalTime.parse(sleepTime.value, formatter)

                // Reset previous error messages
                mealTimeError.value = null
                sleepTimeError.value = null
                wakeUpTimeError.value = null

                if (!wake.isBefore(meal)) {
                    wakeUpTimeError.value = "Wake up time must be before biggest meal time."
                    mealTimeError.value = "Biggest meal time must be after wake-up time."
                    return@Button
                }

                if (!meal.isBefore(sleep)) {
                    mealTimeError.value = "Biggest meal time must be before sleep time."
                    sleepTimeError.value = "Sleep time must be after biggest meal time."
                    return@Button
                }

                // Create a new FoodIntake object with validated data
                val intake = FoodIntake(
                    patientUserId = userId!!,
                    selectedCategories = selectedCategories.joinToString(","), // Convert list to comma-separated string
                    selectedPersona = selectedPersona!!.name,
                    biggestMealTime = biggestMealTime.value,
                    sleepTime = sleepTime.value,
                    wakeUpTime = wakeUpTime.value
                )
                // Insert the food intake record into the database via the ViewModel
                viewModel.insertFoodIntake(intake)

                // Questionnaire completed, set boolean to true
                AuthManager.setQuestionnaireCompleted(true)

                Toast.makeText(context, "Preferences saved!", Toast.LENGTH_SHORT).show()

                // Navigate to Home Screen
                val intent = Intent(context, HomeScreen::class.java)
                context.startActivity(intent)
            },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF579a6b)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Save",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// Referenced from ChatGPT
@Composable
fun CheckboxQuestionnaire(options: List<String>, selectedOptions: MutableList<String>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .height(110.dp)
    ) {
        items(options.size) { index ->
            Row(
                modifier = Modifier.padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedOptions.contains(options[index]),
                    onCheckedChange = {
                        if (it)
                            selectedOptions.add(options[index])
                        else
                            selectedOptions.remove(options[index])
                    },
                    modifier = Modifier
                        .size(20.dp)
                )
                Text(
                    text = options[index],
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(7.dp)
                )
            }
        }
    }
}

// data class for Persona
// Referenced from ChatGPT
data class Persona(
    val name: String,
    val description: String,
    val imageResId: Int
)

// Persona List
// Referenced from ChatGPT
val personas = listOf(
    Persona("Health Devotee", "I’m passionate about healthy eating & health plays a " +
            "big part in my life. I use social media to follow active lifestyle " +
            "personalities or get new recipes/exercise ideas. I may even buy " +
            "superfoods or follow a particular type of diet. I like to think I am " +
            "super healthy.", R.drawable.persona_1),
    Persona("Mindful Eater", "I’m health-conscious and being healthy and eating healthy " +
            "is important to me. Although health means different things to different " +
            "people, I make conscious lifestyle decisions about eating based on what I " +
            "believe healthy means. I look for new recipes and healthy eating information " +
            "on social media.", R.drawable.persona_2),
    Persona("Wellness Striver", "I aspire to be healthy (but struggle sometimes). Healthy " +
            "eating is hard work! I’ve tried to improve my diet, but always find things " +
            "that make it difficult to stick with the changes. Sometimes I notice recipe " +
            "ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go.",
            R.drawable.persona_3),
    Persona("Balance Seeker", "I try and live a balanced lifestyle, and I think that all " +
            "foods are okay in moderation. I shouldn’t have to feel guilty about eating a " +
            "piece of cake now and again. I get all sorts of inspiration from social media " +
            "like finding out about new restaurants, fun recipes and sometimes healthy eating " +
            "tips.", R.drawable.persona_4),
    Persona("Health Procrastinator", "I’m contemplating healthy eating but it’s not a priority " +
            "for me right now. I know the basics about what it means to be healthy, but it " +
            "doesn’t seem relevant to me right now. I have taken a few steps to be healthier " +
            "but I am not motivated to make it a high priority because I have too many other " +
            "things going on in my life.", R.drawable.persona_5),
    Persona("Food Carefree", "I’m not bothered about healthy eating. I don’t really see the " +
            "point and I don’t think about it. I don’t really notice healthy eating tips or " +
            "recipes and I don’t care what I eat.", R.drawable.persona_6)
)

// Referenced from ChatGPT
@Composable
fun ShowButtonAndModal() {
    var selectedPersona by remember { mutableStateOf<Persona?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .height(140.dp)
            .padding(horizontal = 10.dp)
    ) {
        items(personas.size) { index ->
            Button(
                onClick = { selectedPersona = personas[index] },
                modifier = Modifier
                    .padding(2.dp)
                    .height(65.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF579a6b))
            ) {
                Text(
                    text = personas[index].name,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
        }
    }

    // Modal popup (shown only when a persona is selected)
    if (selectedPersona != null) {
        AlertDialog(
            onDismissRequest = { selectedPersona = null },
            confirmButton = {},
            title = {},
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = selectedPersona!!.imageResId),
                        contentDescription = selectedPersona!!.name,
                        modifier = Modifier
                            .size(150.dp)
                            .padding(bottom = 10.dp)
                    )
                    Text(
                        text = selectedPersona!!.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                    )
                    Text(
                        text = selectedPersona!!.description,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { selectedPersona = null },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF579a6b))
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        )
    }
}

// Referenced from W3Lab3 & ChatGPT
@Composable
fun TimePickerRow(label: String, timeState: MutableState<String>, error: String?) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            timeState.value = String.format("%02d:%02d", selectedHour, selectedMinute)
        },
        hour, minute,
        true
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = timeState.value,
                onValueChange = {},
                readOnly = true,
                isError = error != null,
                label = { Text("Select Time", fontSize = 14.sp) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = "Clock icon",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { timePickerDialog.show() }
                    )
                },
                modifier = Modifier
                    .width(120.dp)
                    .clickable { timePickerDialog.show() },
                shape = RoundedCornerShape(8.dp)
            )
        }

        if (error != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp), // optional small padding from right edge
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}