package com.fit2081.nutritrack

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.fit2081.nutritrack.data.AuthManager
import com.fit2081.nutritrack.data.patients.Patient
import com.fit2081.nutritrack.data.patients.PatientViewModel
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.collections.set
import kotlin.sequences.forEach

class InsightScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                val viewModel: PatientViewModel = ViewModelProvider(
                    this, PatientViewModel.PatientViewModelFactory(this)
                )[PatientViewModel::class.java]

                Insights(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Insights(patientViewModel: PatientViewModel) {
    val context = LocalContext.current
    val selectedScreen = remember { mutableStateOf("Insights") }
    val userId = AuthManager.getUserId()
    val patient by patientViewModel.patient.collectAsState()
    val totalScore = getTotalScore(patient)
    val foodCategoryScore = getFoodCategoryScoreFromPatient(patient)
    val beverageCategoryScore = getBeverageCategoryScoreFromPatient(patient)

    LaunchedEffect(userId) {
        if (!userId.isNullOrEmpty()) {
            patientViewModel.loadPatient(userId)
        }
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
                            onClick = {
                                selectedScreen.value = "Home"
                                context.startActivity(Intent(context, HomeScreen::class.java))
                            }
                        )
                        BottomNavItem(
                            icon = painterResource(R.drawable.insights),
                            label = "Insights",
                            isSelected = selectedScreen.value == "Insights",
                            onClick = { selectedScreen.value = "Insights" }
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
                        "Insights: Food Score",
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
                .padding(horizontal = 16.dp)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Food Category Scores
            foodCategoryScore.forEach { (foodCategory, score) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = foodCategory,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .width(110.dp)
                    )
                    // Referenced from W3Lab3
                    Slider(
                        value = score.toFloat(),
                        onValueChange = {},
                        valueRange = 0f..10f,
                        modifier = Modifier
                            .width(200.dp),
                        enabled = false,
                        colors = SliderDefaults.colors(
                            disabledActiveTrackColor = Color(0xFF579a6b),
                            disabledInactiveTrackColor = Color(0xFFc8d9c9)
                        )
                    )
                    Text(
                        text = String.format("%.2f/10", score),
                        fontSize = 15.sp
                    )
                }
            }

            // Beverage Category Scores
            beverageCategoryScore.forEach { (beverageCategory, score) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = beverageCategory,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .width(110.dp)
                    )
                    // Referenced from W3Lab3
                    Slider(
                        value = score.toFloat(),
                        onValueChange = {},
                        valueRange = 0f..5f,
                        modifier = Modifier
                            .width(200.dp),
                        enabled = false,
                        colors = SliderDefaults.colors(
                            disabledActiveTrackColor = Color(0xFF579a6b),
                            disabledInactiveTrackColor = Color(0xFFc8d9c9)
                        )
                    )
                    Text(
                        text = String.format("%.2f/5", score),
                        fontSize = 15.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(40.dp))

            // Total Food Quality Score
            Text(
                text = "Total Food Quality Score",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Referenced from W3Lab3
                Slider(
                    value = totalScore.toFloat(),
                    onValueChange = {},
                    valueRange = 0f..100f,
                    modifier = Modifier
                        .weight(1f),
                    enabled = false,
                    colors = SliderDefaults.colors(
                        disabledActiveTrackColor = Color(0xFF579a6b),
                        disabledInactiveTrackColor = Color(0xFFc8d9c9)
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "$totalScore/100",
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Share Button
            Button(
                onClick = {
                    // Referenced from W4LabD
                    val shareIntent = Intent(ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "My total food quality score is: $totalScore/100")
                    context.startActivity(Intent.createChooser(shareIntent, "Share your score"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF579a6b)),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Share with someone",
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(5.dp))

            // Improve Button
            Button(
                onClick = {
                    context.startActivity(Intent(context, NutriCoachScreen::class.java))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF579a6b)),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.improve),
                    contentDescription = "Improve icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Improve my diet!",
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Returns the total HEIFA score based on the patient's sex.
 *
 * @param patient The patient object containing HEIFA scores.
 * @return The male or female HEIFA total score depending on the patient's sex
 */
fun getTotalScore(patient: Patient?): Double {
    if (patient == null) return 0.0
    return if (patient.sex.equals("male", ignoreCase = true)) {
        patient.HEIFAtotalscoreMale
    } else {
        patient.HEIFAtotalscoreFemale
    }
}

/**
 * Retrieves a map of HEIFA food category scores based on the patient's sex.
 *
 * @param patient The patient object containing HEIFA scores.
 * @return A map where the keys are food category names and the values are the corresponding
 *         HEIFA scores for that category. Returns an empty map if the patient is null.
 *         Scores returned correspond to male or female values depending on patient's sex.
 */
fun getFoodCategoryScoreFromPatient(patient: Patient?): Map<String, Double> {
    if (patient == null) return emptyMap()

    return if (patient.sex.equals("male", ignoreCase = true)) {
        mapOf(
            "Discretionary Foods" to patient.DiscretionaryHEIFAscoreMale,
            "Vegetables" to patient.VegetablesHEIFAscoreMale,
            "Fruits" to patient.FruitHEIFAscoreMale,
            "Grains & Cereals" to patient.GrainsandcerealsHEIFAscoreMale,
            "Whole Grains" to patient.WholegrainsHEIFAscoreMale,
            "Meat & Alternatives" to patient.MeatandalternativesHEIFAscoreMale,
            "Dairy" to patient.DairyandalternativesHEIFAscoreMale,
            "Sodium" to patient.SodiumHEIFAscoreMale,
            "Sugar" to patient.SugarHEIFAscoreMale,
            "Saturated Fat" to patient.SaturatedFatHEIFAscoreMale,
            "Unsaturated Fat" to patient.UnsaturatedFatHEIFAscoreMale
        )
    } else {
        mapOf(
            "Discretionary Foods" to patient.DiscretionaryHEIFAscoreFemale,
            "Vegetables" to patient.VegetablesHEIFAscoreFemale,
            "Fruits" to patient.FruitHEIFAscoreFemale,
            "Grains & Cereals" to patient.GrainsandcerealsHEIFAscoreFemale,
            "Whole Grains" to patient.WholegrainsHEIFAscoreFemale,
            "Meat & Alternatives" to patient.MeatandalternativesHEIFAscoreFemale,
            "Dairy" to patient.DairyandalternativesHEIFAscoreFemale,
            "Sodium" to patient.SodiumHEIFAscoreFemale,
            "Sugar" to patient.SugarHEIFAscoreFemale,
            "Saturated Fat" to patient.SaturatedFatHEIFAscoreFemale,
            "Unsaturated Fat" to patient.UnsaturatedFatHEIFAscoreFemale
        )
    }
}

/**
 * Retrieves a map of HEIFA beverage category scores based on the patient's sex.
 *
 * @param patient The patient object containing HEIFA beverage scores.
 * @return A map where the keys are beverage category names and the values are the corresponding
 *         HEIFA scores for that category. Returns an empty map if the patient is null.
 *         Scores correspond to male or female values depending on the patient's sex.
 */
fun getBeverageCategoryScoreFromPatient(patient: Patient?): Map<String, Double> {
    if (patient == null) return emptyMap()

    return if (patient.sex.equals("male", ignoreCase = true)) {
        mapOf(
            "Alcohol" to patient.AlcoholHEIFAscoreMale,
            "Water" to patient.WaterHEIFAscoreMale
        )
    } else {
        mapOf(
            "Alcohol" to patient.AlcoholHEIFAscoreFemale,
            "Water" to patient.WaterHEIFAscoreFemale
        )
    }
}