package com.fit2081.nutritrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fit2081.nutritrack.data.AuthManager
import com.fit2081.nutritrack.data.patients.Patient
import com.fit2081.nutritrack.data.patients.PatientViewModel
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.sequences.forEach

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: PatientViewModel = ViewModelProvider(
        this, PatientViewModel.PatientViewModelFactory(this)
        )[PatientViewModel::class.java]

        // Initialize the AuthManager with the current context
        AuthManager.init(this)

        // Check if this is the first app launch
        val sharedPrefs = getSharedPreferences("NutriTrackPrefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPrefs.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            sharedPrefs.edit().putBoolean("isFirstLaunch", false).apply()
            lifecycleScope.launch {
                val patients = withContext(Dispatchers.IO) {
                    val list = loadCSVFirstLaunch(applicationContext) // Load patient data from CSV file
                    list
                }
                viewModel.insertAll(patients) // Insert all patients into the database
            }
        }
        setContent {
            NutriTrackTheme {
                WelcomeScreen(viewModel)
            }
        }
    }
}

@Composable
fun WelcomeScreen(viewModel: PatientViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.nutritrack),
            contentDescription = "NutriTrack Logo"
        )

        // Disclaimer Text
        Text(
            text = "This app provides general health and nutrition information for educational " +
                    "purposes only. It is not intended as medical advice, diagnosis, or " +
                    "treatment. Always consult a qualified healthcare professional before " +
                    "making any changes to your diet, exercise, or health regimen.\n" +
                    "Use this app at your own risk.\n" +
                    "If you’d like to an Accredited Practicing Dietitian (APD), please visit " +
                    "the Monash Nutrition/Dietetics Clinic (discounted rates for students):\n" +
                    "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition",
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 25.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))

        // Login Button
        Button(
            onClick = {
                when {
                    AuthManager.isUserLoggedIn() && AuthManager.isQuestionnaireCompleted() -> {
                        context.startActivity(Intent(context, HomeScreen::class.java))
                    }
                    AuthManager.isUserLoggedIn() -> {
                        context.startActivity(Intent(context, FoodQuestionnaire::class.java))
                    }
                    else -> {
                        context.startActivity(Intent(context, LoginScreen::class.java))
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF579a6b)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 10.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Login",
                fontSize = 18.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(80.dp))

        // Student Name + ID
        Text(
            text = "Designed by Reico Kok (33308365)",
            fontSize = 14.sp
        )
    }
}

/**
 * Loads a list of pre-registered patients from a CSV file into the Patient database
 * This function is called only once during the app's first launch to populate the Room database
 * with the pre-registered patient data
 *
 * @param context The context used to access the assets directory
 * @return A list of Patient objects parsed from the CSV file
 */
private fun loadCSVFirstLaunch(context: Context): List<Patient> {
    val patients = mutableListOf<Patient>()
    val inputStream = context.assets.open("data.csv")
    val reader = BufferedReader(InputStreamReader(inputStream))

    reader.useLines { lines ->
        lines.drop(1).forEach { line -> // drop header
            val values = line.split(",")

            val patient = Patient(
                userId = values[1],
                phoneNumber = values[0],
                sex = values[2],
                name = "",
                password = "",

                HEIFAtotalscoreMale = values[3].toDouble(),
                HEIFAtotalscoreFemale = values[4].toDouble(),
                DiscretionaryHEIFAscoreMale = values[5].toDouble(),
                DiscretionaryHEIFAscoreFemale = values[6].toDouble(),
                Discretionaryservesize = values[7].toDouble(),
                VegetablesHEIFAscoreMale = values[8].toDouble(),
                VegetablesHEIFAscoreFemale = values[9].toDouble(),
                Vegetableswithlegumesallocatedservesize = values[10].toDouble(),
                LegumesallocatedVegetables = values[11].toDouble(),
                Vegetablesvariationsscore = values[12].toDouble(),
                VegetablesCruciferous = values[13].toDouble(),
                VegetablesTuberandbulb = values[14].toDouble(),
                VegetablesOther = values[15].toDouble(),
                Legumes = values[16].toDouble(),
                VegetablesGreen = values[17].toDouble(),
                VegetablesRedandorange = values[18].toDouble(),
                FruitHEIFAscoreMale = values[19].toDouble(),
                FruitHEIFAscoreFemale = values[20].toDouble(),
                Fruitservesize = values[21].toDouble(),
                Fruitvariationsscore = values[22].toDouble(),
                FruitPome = values[23].toDouble(),
                FruitTropicalandsubtropical = values[24].toDouble(),
                FruitBerry = values[25].toDouble(),
                FruitStone = values[26].toDouble(),
                FruitCitrus = values[27].toDouble(),
                FruitOther = values[28].toDouble(),
                GrainsandcerealsHEIFAscoreMale = values[29].toDouble(),
                GrainsandcerealsHEIFAscoreFemale = values[30].toDouble(),
                Grainsandcerealsservesize = values[31].toDouble(),
                GrainsandcerealsNonwholegrains = values[32].toDouble(),
                WholegrainsHEIFAscoreMale = values[33].toDouble(),
                WholegrainsHEIFAscoreFemale = values[34].toDouble(),
                Wholegrainsservesize = values[35].toDouble(),
                MeatandalternativesHEIFAscoreMale = values[36].toDouble(),
                MeatandalternativesHEIFAscoreFemale = values[37].toDouble(),
                Meatandalternativeswithlegumesallocatedservesize = values[38].toDouble(),
                LegumesallocatedMeatandalternatives = values[39].toDouble(),
                DairyandalternativesHEIFAscoreMale = values[40].toDouble(),
                DairyandalternativesHEIFAscoreFemale = values[41].toDouble(),
                Dairyandalternativesservesize = values[42].toDouble(),
                SodiumHEIFAscoreMale = values[43].toDouble(),
                SodiumHEIFAscoreFemale = values[44].toDouble(),
                Sodiummgmilligrams = values[45].toDouble(),
                AlcoholHEIFAscoreMale = values[46].toDouble(),
                AlcoholHEIFAscoreFemale = values[47].toDouble(),
                Alcoholstandarddrinks = values[48].toDouble(),
                WaterHEIFAscoreMale = values[49].toDouble(),
                WaterHEIFAscoreFemale = values[50].toDouble(),
                Water = values[51].toDouble(),
                WaterTotalmL = values[52].toDouble(),
                BeverageTotalmL = values[53].toDouble(),
                SugarHEIFAscoreMale = values[54].toDouble(),
                SugarHEIFAscoreFemale = values[55].toDouble(),
                Sugar = values[56].toDouble(),
                SaturatedFatHEIFAscoreMale = values[57].toDouble(),
                SaturatedFatHEIFAscoreFemale = values[58].toDouble(),
                SaturatedFat = values[59].toDouble(),
                UnsaturatedFatHEIFAscoreMale = values[60].toDouble(),
                UnsaturatedFatHEIFAscoreFemale = values[61].toDouble(),
                UnsaturatedFatservesize = values[62].toDouble()
            )
            patients.add(patient)
        }
    }
    return patients
}
