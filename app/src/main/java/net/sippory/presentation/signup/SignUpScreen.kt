package net.sippory.presentation.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {

    var userId by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var confirmedPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val passwordFocusRequester = remember { FocusRequester() }
    val confirmedPasswordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Sippory") }
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserIdTextField(userId = userId, onUserIdChange = { userId = it }, onNext= {
                passwordFocusRequester.requestFocus()
            })
            UserPassWordTextField(
                userPassword = userPassword,
                onUserPasswordChange = { userPassword = it },
                isPasswordVisible = isPasswordVisible,
                onUserPasswordVisibilityChange = { isPasswordVisible = it },
                passwordFocusRequester = passwordFocusRequester,
                onNext = {
                    confirmedPasswordFocusRequester.requestFocus()
                }
            )
            ConfirmedPassWordTextField(
                confirmPassword = confirmedPassword,
                onConfirmPasswordChange = { confirmedPassword = it },
                isPasswordVisible = isPasswordVisible,
                onUserPasswordVisibilityChange = { isPasswordVisible = it },
                passwordFocusRequester = confirmedPasswordFocusRequester,
                focusManager = focusManager
            )
            Button(
                onClick = {
                    navController.navigate("sign-in")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Sign Up", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun UserIdTextField(userId: String, onUserIdChange: (String) -> Unit, onNext: () -> Unit) {
    OutlinedTextField(
        value = userId,
        onValueChange = { onUserIdChange(it) },
        label = { Text("Enter your ID") },
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
        keyboardActions = KeyboardActions(onNext = {
            onNext()
        }),
        trailingIcon = {
            if (userId.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onUserIdChange("")
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
            .padding(horizontal = 16.dp),
    )
}

@Composable
fun UserPassWordTextField(
    userPassword: String,
    onUserPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onUserPasswordVisibilityChange: (Boolean) -> Unit,
    passwordFocusRequester: FocusRequester,
    onNext: () -> Unit
) {
    OutlinedTextField(
        value = userPassword,
        onValueChange = { onUserPasswordChange(it) },
        label = { Text("Enter your Password") },
        keyboardActions = KeyboardActions(onNext = {
            onNext()
        }),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
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
            .padding(horizontal = 16.dp)
            .focusRequester(passwordFocusRequester)
    )
}

@Composable
fun ConfirmedPassWordTextField(
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onUserPasswordVisibilityChange: (Boolean) -> Unit,
    passwordFocusRequester: FocusRequester,
    focusManager: FocusManager
) {
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = { onConfirmPasswordChange(it) },
        label = { Text("Enter your Password") },
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
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
            .padding(horizontal = 16.dp)
            .focusRequester(passwordFocusRequester)
    )
}