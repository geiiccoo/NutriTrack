package com.fit2081.nutritrack

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.fit2081.nutritrack.data.patients.Patient
import com.fit2081.nutritrack.data.patients.PatientViewModel
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                val viewModel: PatientViewModel = ViewModelProvider(
                    this, PatientViewModel.PatientViewModelFactory(this)
                )[PatientViewModel::class.java]

                Register(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(patientViewModel: PatientViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMismatchError by remember { mutableStateOf(false) }
    var userIdError by remember { mutableStateOf(false) }
    var phoneNumberError by remember { mutableStateOf(false) }

    val allPatients by patientViewModel.allPatients.collectAsState(initial = emptyList())
    val allUserIds = allPatients.map { it.userId }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        // Register
        Text(
            text = "Register",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown Menu for User ID
        // Referenced from LoginScreen
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
                isError = userIdError,
                supportingText = {
                    if (userIdError) Text("Invalid user ID or phone number", color = Color.Red)
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
                allUserIds.forEach { id ->
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
        Spacer(modifier = Modifier.height(5.dp))

        // Phone Number text field
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = phoneNumberError,
            supportingText = {
                if (phoneNumberError) Text("Invalid User ID or Phone Number", color = Color.Red)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))

        // Name text field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your Name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Password text field
        SecureTextField(
            label = "Password",
            password = password,
            onPasswordChange = { password = it }
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Confirm Password text field
        SecureTextField(
            label = "Confirm Password",
            password = confirmPassword,
            onPasswordChange = {
                confirmPassword = it
                passwordMismatchError = password != confirmPassword
            },
            isError = passwordMismatchError,
            supportingText = {
                if (passwordMismatchError) {
                    Text("Passwords do not match", color = Color.Red)
                }
            }
        )
        Spacer(modifier = Modifier.height(5.dp))

        // Disclaimer Text
        Text(
            text = "This app is only for pre-registered users. Please enter your ID, " +
                    "phone number, name, and password to claim your account.",
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Register Button
        Button(
            onClick = {
                if (userId.isBlank() || phoneNumber.isBlank() || name.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Reset errors first
                userIdError = false
                phoneNumberError = false
                passwordMismatchError = false

                if (password != confirmPassword) {
                    passwordMismatchError = true
                    return@Button
                }

                // Launch coroutine to perform database operations
                scope.launch {
                    try {
                        var user: Patient? = null
                        // Perform patient lookup and update in IO dispatcher
                        withContext(Dispatchers.IO) {
                            user = patientViewModel.getPatientByUserIdAndPhone(userId, phoneNumber)
                            if (user != null) {
                                val updatedUser = user.copy(name = name, password = password)
                                patientViewModel.update(updatedUser)

                                patientViewModel.loadPatient(userId)
                            }
                        }

                        if (user == null) {
                            userIdError = true
                            phoneNumberError = true
                        } else {
                            Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "An error occurred: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
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
                text = "Register",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        Button(
            onClick = {
                val intent = Intent(context, LoginScreen::class.java)
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
                text = "Login",
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
 * @param label The label displayed inside the text field
 * @param password The current text value of the password input
 * @param onPasswordChange Callback triggered when the user changes the password input
 * @param isError Boolean to indicate whether the current input is in an error state
 * @param supportingText Optional composable to show supporting or error text below the field
 */
// Generated from ChatGPT
@Composable
fun SecureTextField(
    label: String,
    password: String,
    onPasswordChange: (String) -> Unit,
    isError: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
        isError = isError,
        supportingText = supportingText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    )
}