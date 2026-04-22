package com.datoban.rich_uncle.visual.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LobbyScreen(
    onCreateRoom: (String) -> Unit,
    onJoinRoom: (String) -> Unit,
    onStartGame: (String) -> Unit,
    onLogout: () -> Unit
) {
    var roomId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Lobby",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = roomId,
            onValueChange = { roomId = it },
            label = { Text("ID de sala") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onCreateRoom(roomId) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear sala")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onJoinRoom(roomId) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Unirse a sala")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onStartGame(roomId) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar juego")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onLogout() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salir")
        }
    }
}