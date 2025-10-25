package com.fit2081.nutritrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.nutritrack.data.AuthManager
import com.fit2081.nutritrack.data.patients.PatientViewModel
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                val viewModel: PatientViewModel = ViewModelProvider(
                    this, PatientViewModel.PatientViewModelFactory(this)
                )[PatientViewModel::class.java]

                Login(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(patientViewModel: PatientViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    val allPatients by patientViewModel.allPatients.collectAsState(initial = emptyList())
    val registeredPatients = allPatients.filter { !it.password.isNullOrBlank() }
    val registeredUserIds = registeredPatients.map { it.userId }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        // Log In
        Text(
            text = "Log In",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown Menu for User ID
        // Referenced from ChatGPT
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            TextField(
                value = userId,
                onValueChange = {},
                readOnly = true,
                label = { Text("My ID (Provided by Your Clinician)") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
            )

            // Referenced from LoginScreen
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                registeredUserIds.forEach { id ->
                    DropdownMenuItem(
                        text = { Text(id) },
                        onClick = {
                            userId = id
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        // Password text field
        SecureTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isError = passwordError,
            errorMessage = "Invalid password",
            onDone = {
                // Trigger the same login logic as the button
                if (userId.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@SecureTextField
                }

                passwordError = false

                // Launch coroutine to perform login logic on background thread
                scope.launch {
                    val user = withContext(Dispatchers.IO) {
                        // Attempt to fetch user from database using userId
                        patientViewModel.getPatientByUserId(userId)
                    }
                    if (user != null && user.password == password) {
                        AuthManager.login(context, userId)

                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()

                        // Navigate to appropriate screen based on questionnaire completion
                        if (AuthManager.isQuestionnaireCompleted()) {
                            context.startActivity(Intent(context, HomeScreen::class.java))
                        } else {
                            context.startActivity(Intent(context, FoodQuestionnaire::class.java))
                        }
                    } else {
                        passwordError = true
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Disclaimer Text
        Text(
            text = "This app is only for pre-registered users. Please enter your ID, " +
                    "and password or Register to claim your account on your first visit.",
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Continue Button
        Button(
            onClick = {
                if (userId.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Reset error first
                passwordError = false

                scope.launch {
                    val user = withContext(Dispatchers.IO) {
                        patientViewModel.getPatientByUserId(userId)
                    }
                    if (user != null && user.password == password) {
                        AuthManager.login(context, userId)

                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                        if (AuthManager.isQuestionnaireCompleted()) {
                            context.startActivity(Intent(context, HomeScreen::class.java))
                        } else {
                            context.startActivity(Intent(context, FoodQuestionnaire::class.java))
                        }
                    } else {
                        passwordError = true
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
                text = "Continue",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register Button
        Button(
            onClick = {
                val intent = Intent(context, RegisterScreen::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF579a6b)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 10.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Register",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * A composable function that displays a password input field with a visibility toggle icon
 * This field hides the input by default and allows users to toggle visibility using an icon
 *
 * @param value The current text entered in the field
 * @param onValueChange Callback triggered when the text value changes
 * @param label The label to display inside the text field
 * @param isError Boolean to indicate whether the current input is in an error state
 * @param errorMessage The message to display below the text field when `isError` is true
 * @param onDone A callback triggered when the user presses "Enter" on the keyboard
 */
@Composable
fun SecureTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorMessage: String = "Invalid password",
    onDone: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done // <-- Set IME action to Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone() } // <-- Trigger onDone when Enter is pressed
        ),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Image(
                    painter = painterResource(
                        id = if (passwordVisible)
                            R.drawable.visibility
                        else
                            R.drawable.visibility_off
                    ),
                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
            }
        },
        supportingText = {
            if (isError) Text(errorMessage, color = Color.Red)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        singleLine = true
    )
}