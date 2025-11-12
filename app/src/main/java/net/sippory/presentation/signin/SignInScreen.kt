package net.sippory.presentation.signin

import net.sippory.R
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavController) {
    var userId by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val passwordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.sippory_logo),
                contentDescription = "sippory logo",
                modifier = Modifier
                    .width(200.dp)
                    .height(156.dp)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )

            UserIdTextField(
                userId = userId,
                onUserIdChange = { newId -> userId = newId },
                onNext = { passwordFocusRequester.requestFocus() })
            UserPasswordTextField(
                userPassword = userPassword,
                onUserPasswordChange = { newPassword -> userPassword = newPassword },
                isPasswordVisible = isPasswordVisible,
                onUserPasswordVisibilityChange = { isVisible -> isPasswordVisible = isVisible },
                passwordFocusRequester = passwordFocusRequester,
                focusManager = focusManager
            )
            SignInButton(onClick = { navController.navigate("home") })
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account?")
                SignUpButton(navController = navController)
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
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(onNext = {
            onNext()
        }),
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription =
                    "User ID Icon"
            )
        },
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
fun UserPasswordTextField(
    userPassword: String,
    onUserPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onUserPasswordVisibilityChange: (Boolean) -> Unit,
    passwordFocusRequester: FocusRequester,
    focusManager: FocusManager
) {
    OutlinedTextField(
        value = userPassword,
        onValueChange = { onUserPasswordChange(it) },
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

@Composable
fun SignInButton(onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun SignUpButton(navController: NavController) {
    TextButton(
        onClick = { navController.navigate("sign-up") }
    ) {
        Text("Sign Up", color = Color.Black)
    }
}