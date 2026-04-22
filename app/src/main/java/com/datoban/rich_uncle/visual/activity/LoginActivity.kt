package com.datoban.rich_uncle.visual.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.*
import com.datoban.rich_uncle.visual.ViewModel.LoginViewModel
import com.datoban.rich_uncle.visual.screens.LoginScreen
import com.datoban.rich_uncle.visual.screens.SignUpScreen

class LoginActivity : ComponentActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        viewModel.error.observe(this) { msg ->
            if (msg.isNotEmpty()) {
                android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        setContent {

            var currentScreen by remember { mutableStateOf("login") }

            when (currentScreen) {

                "login" -> LoginScreen(
                    viewModel = viewModel,
                    onGoToSignUp = { currentScreen = "signup" },
                    onLoginSuccess = {
                        startActivity(Intent(this, LobbyActivity::class.java))
                        finish() // opcional (evita volver al login)
                    }
                )

                "signup" -> SignUpScreen(
                    viewModel = viewModel,
                    onBackToLogin = { currentScreen = "login" }
                )
            }
        }
    }
}