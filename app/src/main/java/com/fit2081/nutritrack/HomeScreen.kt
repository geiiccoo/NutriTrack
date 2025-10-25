package com.fit2081.nutritrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.fit2081.nutritrack.data.AuthManager
import com.fit2081.nutritrack.data.patients.PatientViewModel
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme
import java.io.BufferedReader
import java.io.InputStreamReader

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                val viewModel: PatientViewModel = ViewModelProvider(
                    this, PatientViewModel.PatientViewModelFactory(this)
                )[PatientViewModel::class.java]

                Home(viewModel)
            }
        }
    }
}

@Composable
fun Home(patientViewModel: PatientViewModel) {
    val context = LocalContext.current
    val selectedScreen = remember { mutableStateOf("Home") }
    val userId = AuthManager.getUserId()
    val patient by patientViewModel.patient.collectAsState()

    LaunchedEffect(userId) {
        if (!userId.isNullOrEmpty()) {
            // Load the patient data from the database using the provided userId
            patientViewModel.loadPatient(userId)
        }
    }

    val name = patient?.name

    // Determine the appropriate HEIFA total score based on the patient's sex
    val foodScore = when (patient?.sex?.lowercase()) {
        "male" -> patient?.HEIFAtotalscoreMale ?: 0.0
        "female" -> patient?.HEIFAtotalscoreFemale ?: 0.0
        else -> 0.0
    }

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
                            onClick = { selectedScreen.value = "Home" }
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
                            onClick = {
                                selectedScreen.value = "NutriCoach"
                                context.startActivity(Intent(context, NutriCoachScreen::class.java))
                            }
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(5.dp))

            // Personalized Greeting
            Text(
                text = "Hello,",
                fontSize = 16.sp,
                color = Color.Gray
            )
            // User's name
            Text(
                text = name.toString(),
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Edit button
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "You've already filled in your Food Intake Questionnaire, but you " +
                    "can change details here:",
                    fontSize = 12.sp,
                    modifier = Modifier
                        .weight(1f)
                )
                Button(onClick = {
                    val intent = Intent(context, FoodQuestionnaire::class.java)
                    context.startActivity(intent)
                },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF579a6b)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Edit icon",
                        Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Edit",
                        fontSize = 15.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))

            Image(
                painter = painterResource(R.drawable.balanced_meal),
                contentDescription = "Balanced Meal",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(280.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))

            // Food Score Display
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .weight(1f)
                )
                Row(modifier = Modifier
                    .clickable(onClick = {
                        val intent = Intent(context, InsightScreen::class.java)
                        context.startActivity(intent)
                    })) {
                    Text(
                        text = "See all scores",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Right Arrow",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(25.dp)
                    )}
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Total Food Quality Score
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = if (foodScore > 50.0) painterResource(R.drawable.arrow_up)
                    else painterResource(R.drawable.arrow_down),
                    contentDescription = "Arrow",
                    modifier = Modifier
                        .size(35.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Your Food Quality Score",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = "$foodScore/100",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (foodScore >= 50.0) Color(0xFF008000) else Color(0xFFFF0000)
                )
            }
            HorizontalDivider(
                color = Color.LightGray,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .padding(vertical = 20.dp)
            )

            // Explanation of Food Quality Score
            Text(
                text = "What is the Food Quality Score?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
            )
            Text(
                text = "Your Food Quality Score provides a snapshot of how well your eating " +
                "patterns align with established food guidelines, helping you identify both " +
                "strengths and opportunities for improvement in your diet.\n\n" +
                "This personalized measurement considers various food groups including " +
                "vegetables, fruits, whole grains, and proteins to give you practical " +
                "insights for making healthier food choices.",
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
            )
        }
    }
}

// Referenced from ChatGPT
@Composable
fun BottomNavItem(icon: Any, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val selectedColor = Color.White
    val defaultColor = Color(0xFFa8c7af)
    val backgroundColor = if (isSelected) Color.Gray else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        if (icon is ImageVector) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) selectedColor else defaultColor,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(backgroundColor)
                    .padding(horizontal = 8.dp)
                    .padding(vertical = 2.dp)
            )
        } else if (icon is Painter) {
            Image(
                painter = icon,
                contentDescription = label,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(backgroundColor)
                    .padding(horizontal = 8.dp)
                    .padding(vertical = 2.dp),
                colorFilter = ColorFilter.tint(if (isSelected) selectedColor else defaultColor)
            )
        }
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (isSelected) selectedColor else defaultColor,
            textAlign = TextAlign.Center
        )
    }
}