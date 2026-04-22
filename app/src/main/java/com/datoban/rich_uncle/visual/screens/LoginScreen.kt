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
fun LoginScreen(
    viewModel: LoginViewModel,
    onGoToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit   // 👈 NUEVO
) {
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))

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
            onClick = {
                viewModel.login()
                onLoginSuccess()   // 👈 AQUÍ NAVEGAS
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        TextButton(onClick = onGoToSignUp) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}