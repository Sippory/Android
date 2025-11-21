package net.sippory.presentation.signup

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    var userEmailId by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var confirmedPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var isUserEmailIdError by remember { mutableStateOf(false) }
    var isUserPasswordError by remember { mutableStateOf(false) }
    var isConfirmedPasswordError by remember { mutableStateOf(false) }

    val isEmailValid by remember(userEmailId) {
        derivedStateOf {
            Patterns.EMAIL_ADDRESS.matcher(userEmailId).matches()
        }
    }

    val isPasswordValid by remember(userPassword) {
        derivedStateOf {
            userPassword.length >= 6
        }
    }


    val isPasswordMatching by remember {
        derivedStateOf {
            userPassword == confirmedPassword
        }
    }

    val isFormValid by remember {
        derivedStateOf {
            userEmailId.isNotBlank() &&
                    userPassword.isNotBlank() &&
                    confirmedPassword.isNotBlank() &&
                    isPasswordMatching && isEmailValid
        }
    }

    val passwordFocusRequester = remember { FocusRequester() }
    val confirmedPasswordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val auth: FirebaseAuth = Firebase.auth
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sippory") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(
                modifier = Modifier.padding(40.dp)
            )
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Sign UP", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("Just a few quick things to get started", fontWeight = FontWeight.Bold)
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UserEmailIdTextField(
                        userEmailId = userEmailId,
                        onUserEmailIdChange = { userEmailId = it },
                        onNext = {
                            passwordFocusRequester.requestFocus()
                        },
                        isError = isUserEmailIdError,
                        isEmailValid = isEmailValid,
                        onErrorChange = { isUserEmailIdError = it },
                    )
                    UserPassWordTextField(
                        userPassword = userPassword,
                        onUserPasswordChange = { userPassword = it },
                        isPasswordVisible = isPasswordVisible,
                        onUserPasswordVisibilityChange = { isPasswordVisible = it },
                        passwordFocusRequester = passwordFocusRequester,
                        onNext = {
                            confirmedPasswordFocusRequester.requestFocus()
                        },
                        isError = isUserPasswordError,
                        isPasswordValid = isPasswordValid,
                        onErrorChange = { isUserPasswordError = it }
                    )
                    ConfirmedPassWordTextField(
                        confirmPassword = confirmedPassword,
                        onConfirmPasswordChange = { confirmedPassword = it },
                        isPasswordVisible = isPasswordVisible,
                        onUserPasswordVisibilityChange = { isPasswordVisible = it },
                        passwordFocusRequester = confirmedPasswordFocusRequester,
                        focusManager = focusManager,
                        isError = isConfirmedPasswordError,
                        onErrorChange = { isConfirmedPasswordError = it },
                        isPasswordMatchError = !isPasswordMatching
                    )
                }
                Column {
                    SignUpButton(
                        navController = navController, isFormValid = isFormValid,
                        onClick = {
                            coroutineScope.launch {
                                auth.createUserWithEmailAndPassword(userEmailId, userPassword)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "Sign Up Successful",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.navigate("sign-in") {
                                                popUpTo("sign-up") {
                                                    inclusive = true
                                                }
                                            }
                                        } else {
                                            val errorMessage = "Sign Up Failed"
                                            Toast.makeText(
                                                context,
                                                errorMessage,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                            }
                        })
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Already have an account?")
                        SignInButton(navController = navController)
                    }
                }

            }

        }
    }
}

@Composable
fun UserEmailIdTextField(
    userEmailId: String,
    onUserEmailIdChange: (String) -> Unit,
    onNext: () -> Unit,
    isError: Boolean = false,
    isEmailValid: Boolean,
    onErrorChange: (Boolean) -> Unit = {},
) {
    var isInputFocusChanged by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = userEmailId,
            onValueChange = { onUserEmailIdChange(it); onErrorChange(false) },
            label = { Text("Enter Email ID") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription =
                        "User ID Icon"
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            isError = isError || (isInputFocusChanged && !isEmailValid && userEmailId.isNotBlank()),
            keyboardActions = KeyboardActions(onNext = {
                if (userEmailId.isBlank()) {
                    onErrorChange(true)
                } else if (!isEmailValid) {
                    onErrorChange(true)
                }
                onNext()
            }),
            trailingIcon = {
                if (userEmailId.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onUserEmailIdChange("")
                        }) {
                        Icon(
                            imageVector = Icons.Filled.HighlightOff, contentDescription = "Clear ID"
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        isInputFocusChanged = true
                    } else if (isInputFocusChanged && (!focusState.isFocused && userEmailId.isBlank() || !isEmailValid)) {
                        onErrorChange(true)
                    }

                }

        )
        if (isError && userEmailId.isBlank() && isInputFocusChanged) {
            Text(
                "Please enter Email ID",
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        } else if (userEmailId.isNotEmpty() && !isEmailValid && isInputFocusChanged) {
            Text(
                "Invalid Email Format",
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun UserPassWordTextField(
    userPassword: String,
    onUserPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onUserPasswordVisibilityChange: (Boolean) -> Unit,
    passwordFocusRequester: FocusRequester,
    onNext: () -> Unit,
    isError: Boolean,
    isPasswordValid: Boolean,
    onErrorChange: (Boolean) -> Unit
) {
    var isInputFocusChanged by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = userPassword,
            onValueChange = { onUserPasswordChange(it); onErrorChange(false) },
            label = { Text("Enter new Password") },
            keyboardActions = KeyboardActions(onNext = {
                if (userPassword.isEmpty()) {
                    onErrorChange(true)
                } else if (!isPasswordValid) {
                    onErrorChange(true)
                }
                onNext()
            }),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            isError = isError || isPasswordValid,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription =
                        "Password Icon"
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onUserPasswordVisibilityChange(!isPasswordVisible)
                    }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Show Password" else "Hide Password"
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        isInputFocusChanged = true
                    } else if (isInputFocusChanged) {
                        if (userPassword.isBlank() || !isPasswordValid) {
                            onErrorChange(true)
                        }
                    }

                }
        )
        if (isError && isInputFocusChanged) {
            Text(
                "Please enter password",
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        } else if (!isPasswordValid && isInputFocusChanged) {
            Text(
                "Password must be at least 6 characters",
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun ConfirmedPassWordTextField(
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onUserPasswordVisibilityChange: (Boolean) -> Unit,
    passwordFocusRequester: FocusRequester,
    focusManager: FocusManager,
    isError: Boolean,
    onErrorChange: (Boolean) -> Unit,
    isPasswordMatchError: Boolean,
) {
    var isFocusChanged by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { onConfirmPasswordChange(it); onErrorChange(false) },
            label = { Text("Enter Confirm Password") },
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            isError = isError || isPasswordMatchError,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription =
                        "Password Icon"
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onUserPasswordVisibilityChange(!isPasswordVisible)
                    }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Show Password" else "Hide Password"
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        isFocusChanged = true
                    } else if (!focusState.isFocused && confirmPassword.isBlank() && isFocusChanged) {
                        onErrorChange(true)
                    }
                }
        )
        if (isError) {
            Text(
                "Please enter confirm password",
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        } else if (isPasswordMatchError && isFocusChanged) {
            Text(
                "Passwords do not match",
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

    }
}

@Composable
fun SignUpButton(navController: NavController, isFormValid: Boolean, onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        enabled = isFormValid,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(Color.Black)
    ) {
        Text("Sign Up", fontSize = 18.sp)
    }
}

@Composable
fun SignInButton(navController: NavController) {
    TextButton(
        onClick = { navController.navigate("sign-in") }
    ) {
        Text("Sign In", color = Color.Black)
    }
}
