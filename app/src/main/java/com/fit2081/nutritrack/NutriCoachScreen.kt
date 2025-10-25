package com.fit2081.nutritrack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.fit2081.nutritrack.data.fruityviceapi.FruitViewModel
import com.fit2081.nutritrack.data.genai.GenAIViewModel
import com.fit2081.nutritrack.data.genai.UIState
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme

class NutriCoachScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                val fruitViewModel: FruitViewModel = ViewModelProvider(
                    this, FruitViewModel.FruitViewModelFactory(this)
                )[FruitViewModel::class.java]

                val genAIViewModel: GenAIViewModel = ViewModelProvider(
                    this, GenAIViewModel.GenAIViewModelFactory(this)
                )[GenAIViewModel::class.java]

                NutriCoach(fruitViewModel, genAIViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutriCoach(fruitViewModel: FruitViewModel, genAIViewModel : GenAIViewModel) {
    val context = LocalContext.current
    val selectedScreen = remember { mutableStateOf("NutriCoach") }
    var fruitInput by remember { mutableStateOf(TextFieldValue("")) }
    val fruit by fruitViewModel.fruit.collectAsState()
    val isLoading by fruitViewModel.isLoading.collectAsState()
    val fruitDetails = listOf(
        "Family" to fruit?.family,
        "Calories" to fruit?.nutritions?.calories,
        "Fat" to fruit?.nutritions?.fat,
        "Sugar" to fruit?.nutritions?.sugar,
        "Carbohydrates" to fruit?.nutritions?.carbohydrates,
        "Protein" to fruit?.nutritions?.protein
    )
    val errorMessage by fruitViewModel.errorMessage.collectAsState()
    val uiState by genAIViewModel.uiState.collectAsState()
    var message by remember { mutableStateOf("") }
    val tips by genAIViewModel.savedTips.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        // Referenced from W2Lab3
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .height(105.dp),
                containerColor = Color(0xFF1b5a45),
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomNavItem(
                            icon = Icons.Filled.Home,
                            label = "Home",
                            isSelected = selectedScreen.value == "Home",
                            onClick = {
                                selectedScreen.value = "Home"
                                context.startActivity(Intent(context, HomeScreen::class.java))
                            }
                        )
                        BottomNavItem(
                            icon = painterResource(R.drawable.insights),
                            label = "Insights",
                            isSelected = selectedScreen.value == "Insights",
                            onClick = {
                                selectedScreen.value = "Insights"
                                context.startActivity(Intent(context, InsightScreen::class.java))
                            }
                        )
                        BottomNavItem(
                            icon = Icons.Default.Person,
                            label = "NutriCoach",
                            isSelected = selectedScreen.value == "NutriCoach",
                            onClick = { selectedScreen.value = "NutriCoach" }
                        )
                        BottomNavItem(
                            icon = Icons.Default.Settings,
                            label = "Settings",
                            isSelected = selectedScreen.value == "Settings",
                            onClick = {
                                selectedScreen.value = "Settings"
                                context.startActivity(Intent(context, SettingScreen::class.java))
                            }
                        )
                    }
                }
            )
        },
        // Referenced from W2Lab4
        topBar ={
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1b5a45),
                    titleContentColor = Color.White
                ),
                title ={
                    Text(
                        "NutriCoach",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val intent = Intent(context, HomeScreen::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Arrow back button",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Fruit Name
            Text(
                text = "Fruit Name",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Search bar and button
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = fruitInput,
                    onValueChange = { fruitInput = it },
                    placeholder = { Text("Enter fruit name") },
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (fruitInput.text.isBlank()) {
                                Toast.makeText(context, "Please enter a fruit name", Toast.LENGTH_SHORT).show()
                            } else {
                                fruitViewModel.fetchFruitInfo(fruitInput.text)
                            }
                        }
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(55.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))

                Button(onClick = {
                    if (fruitInput.text.isBlank()) {
                        Toast.makeText(context, "Please enter a fruit name", Toast.LENGTH_SHORT).show()
                    } else {
                        fruitViewModel.fetchFruitInfo(fruitInput.text)
                    }
                },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF579a6b)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Details",
                        fontSize = 18.sp
                    )
                }
            }
            // Error message
            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Fruit facts and stats
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                fruitDetails.forEach { (label, value) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(15.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.weight(1f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = ":".padEnd(5),
                                modifier = Modifier.padding(horizontal = 4.dp),
                                fontSize = 16.sp
                            )
                            Text(
                                text = value?.toString() ?: "-",
                                modifier = Modifier.weight(1f),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
            HorizontalDivider(
                color = Color.LightGray,
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 10.dp)
            )

            // Generate motivational message button
            Button(
                onClick = {
                    genAIViewModel.generateMotivationalMessage()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF579a6b)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.chat_bubble),
                    contentDescription = "Chat icon"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Motivational Message (AI)",
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(5.dp))

            // Motivational message
            // Referenced from W7 GenAI
            when (uiState) {
                is UIState.Loading -> {
                    CircularProgressIndicator()
                }
                is UIState.Success -> {
                    message = (uiState as UIState.Success).outputText
                    Text(
                        text = message,
                        fontSize = 16.sp
                    )
                }
                is UIState.Error -> {
                    Text(
                        text = (uiState as UIState.Error).errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                else -> {}
            }

            // Dialog of all the tips generated
            // Referenced from FoodQuestionnaire screen
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text("AI Tips")
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .fillMaxWidth()
                        ) {
                            if (tips.isEmpty()) {
                                Text("No tips saved yet.")
                            } else {
                                tips.forEachIndexed { index, tip ->
                                    OutlinedTextField(
                                        value = tip.message,
                                        onValueChange = {},
                                        label = { Text("Tip ${index + 1}") },
                                        readOnly = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF579a6b)),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text("Done")
                        }
                    }
                )
            }
        }
    }
    // Show all tips button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 115.dp)
    ) {
        Button(
            onClick = {
                genAIViewModel.loadAllTips()
                showDialog = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF579a6b)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            Text(
                text = "Show All Tips",
                fontSize = 16.sp
            )
        }
    }
}