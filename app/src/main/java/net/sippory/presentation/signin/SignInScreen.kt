package net.sippory.presentation.signin

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import net.sippory.R

private val DeepBlack = Color(0xFF0D0D0D)
private val WineRed = Color(0xFF8B1538)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavController) {
    var userEmailId by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val isFormValid by remember(userEmailId, userPassword) {
        derivedStateOf {
            userEmailId.isNotBlank() && userPassword.isNotBlank()
        }
    }

    val passwordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val auth: FirebaseAuth = Firebase.auth
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sippory") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlack, titleContentColor = Color.White),
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(DeepBlack),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(24.dp),
            )
            Image(
                painter = painterResource(id = R.drawable.sippory_logo),
                contentDescription = "sippory logo",
                modifier =
                    Modifier
                        .width(200.dp)
                        .height(156.dp),
            )
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(24.dp),
            )

            UserEmailIdTextField(
                userEmailId = userEmailId,
                onUserEmailIdChange = { newId -> userEmailId = newId },
                onNext = { passwordFocusRequester.requestFocus() },
            )
            UserPasswordTextField(
                userPassword = userPassword,
                onUserPasswordChange = { newPassword -> userPassword = newPassword },
                isPasswordVisible = isPasswordVisible,
                onUserPasswordVisibilityChange = { isVisible -> isPasswordVisible = isVisible },
                passwordFocusRequester = passwordFocusRequester,
                focusManager = focusManager,
            )
            SignInButton(isEnable = isFormValid, onClick = {
                coroutineScope.launch {
                    signIn(
                        auth = auth,
                        userEmailId = userEmailId,
                        userPassword = userPassword,
                        onSuccess = {
                            Toast.makeText(context, "Sign In Successful", Toast.LENGTH_SHORT).show()
                            navController.navigate("home") {
                                popUpTo("sign-in") { inclusive = true }
                            }
                        },
                        onFailure = {
                            Toast.makeText(context, "Sign In Failed", Toast.LENGTH_SHORT).show()
                        },
                    )
                }
            })
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Don't have an account?", color = Color.Gray)
                SignUpButton(navController = navController)
            }
        }
    }
}

@Composable
fun UserEmailIdTextField(
    userEmailId: String,
    onUserEmailIdChange: (String) -> Unit,
    onNext: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "Email ID", color = Color.White)
        OutlinedTextField(
            value = userEmailId,
            onValueChange = { onUserEmailIdChange(it) },
            placeholder = { Text("Enter your Email ID", color = Color.Gray) },
            textStyle = TextStyle(color = Color.White),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WineRed,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = WineRed,
                ),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
            keyboardActions =
                KeyboardActions(onNext = {
                    onNext()
                }),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription =
                        "User ID Icon",
                    tint = Color.Gray,
                )
            },
            trailingIcon = {
                if (userEmailId.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onUserEmailIdChange("")
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.HighlightOff,
                            contentDescription = "Clear ID",
                            tint = Color.Gray,
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun UserPasswordTextField(
    userPassword: String,
    onUserPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onUserPasswordVisibilityChange: (Boolean) -> Unit,
    passwordFocusRequester: FocusRequester,
    focusManager: FocusManager,
) {
    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "Password", color = Color.White)
        OutlinedTextField(
            value = userPassword,
            onValueChange = { onUserPasswordChange(it) },
            placeholder = { Text("Enter your Password", color = Color.Gray) },
            textStyle = TextStyle(color = Color.White),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WineRed,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = WineRed,
                ),
            keyboardActions =
                KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription =
                        "Password Icon",
                    tint = Color.Gray,
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onUserPasswordVisibilityChange(!isPasswordVisible)
                    },
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Show Password" else "Hide Password",
                        tint = Color.Gray,
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester),
        )
    }
}

@Composable
fun SignInButton(
    isEnable: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = {
            onClick()
        },
        enabled = isEnable,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = WineRed, disabledContainerColor = Color.DarkGray),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    }
}

@Composable
fun SignUpButton(navController: NavController) {
    TextButton(
        onClick = { navController.navigate("sign-up") },
    ) {
        Text("Sign Up", color = WineRed)
    }
}

private fun signIn(
    auth: FirebaseAuth,
    userEmailId: String,
    userPassword: String,
    onSuccess: () -> Unit,
    onFailure: () -> Unit,
) {
    auth.signInWithEmailAndPassword(userEmailId, userPassword)
        .addOnCompleteListener { task ->
            when {
                task.isSuccessful -> onSuccess()
                else -> onFailure()
            }
        }
}
