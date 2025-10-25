package com.fit2081.nutritrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.fit2081.nutritrack.data.AuthManager
import com.fit2081.nutritrack.data.patients.PatientViewModel
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme

class SettingScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                val viewModel: PatientViewModel = ViewModelProvider(
                    this, PatientViewModel.PatientViewModelFactory(this)
                )[PatientViewModel::class.java]

                Settings(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(patientViewModel: PatientViewModel) {
    val context = LocalContext.current
    val selectedScreen = remember { mutableStateOf("Settings") }
    val userId = AuthManager.getUserId()
    val patient by patientViewModel.patient.collectAsState()

    LaunchedEffect(userId) {
        if (!userId.isNullOrEmpty()) {
            patientViewModel.loadPatient(userId)
        }
    }

    val name = patient?.name
    val phone = patient?.phoneNumber
    val id = patient?.userId

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
                            onClick = {
                                selectedScreen.value = "NutriCoach"
                                context.startActivity(Intent(context, NutriCoachScreen::class.java))
                            }
                        )
                        BottomNavItem(
                            icon = Icons.Default.Settings,
                            label = "Settings",
                            isSelected = selectedScreen.value == "Settings",
                            onClick = { selectedScreen.value = "Settings" }
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
                        "Settings",
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
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // User info section
            Text(
                text = "ACCOUNT",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // User's name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Person icon",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(30.dp))
                Text(
                    text = name.toString(),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // User's phone
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Person icon",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(30.dp))
                Text(
                    text = phone.toString(),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // User's id
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Person icon",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(30.dp))
                Text(
                    text = id.toString(),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(
                color = Color.LightGray,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(vertical = 20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Other settings section
            Text(
                text = "OTHER SETTINGS",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Logout button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(50.dp)
                    .clickable{
                        AuthManager.logout(context)
                        val intent = Intent(context, LoginScreen::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout icon",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(30.dp))
                Text(
                    text = "Logout",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Arrow right",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}