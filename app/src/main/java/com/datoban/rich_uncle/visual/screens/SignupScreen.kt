package com.datoban.rich_uncle.visual.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.datoban.rich_uncle.visual.ViewModel.LoginViewModel

@Composable
fun SignUpScreen(
    viewModel: LoginViewModel,
    onBackToLogin: () -> Unit
) {
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val username by viewModel.username.observeAsState("")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Crear Cuenta", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { viewModel.onUsernameChange(it) },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.signUp() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }

        TextButton(onClick = onBackToLogin) {
            Text("Ya tengo cuenta")
        }
    }
}