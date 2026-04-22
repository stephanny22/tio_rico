package com.datoban.rich_uncle.visual.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.livedata.observeAsState
import com.datoban.rich_uncle.data.model.Action
import com.datoban.rich_uncle.data.model.ChatMessage
import com.datoban.rich_uncle.visual.ViewModel.GameViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    roomId: String,
    playerId: String,
    onNavigateResult: (Boolean) -> Unit
) {
    val state by viewModel.gameState.observeAsState()
    val messages by viewModel.messages.observeAsState(emptyList())
    val actionResult by viewModel.actionResult.observeAsState("")
    val eventMessage by viewModel.eventMessage.observeAsState("")
    val gameOver by viewModel.gameOver.observeAsState("")

    var chatInput by remember { mutableStateOf("") }

    val context = LocalContext.current

    val myUserId = FirebaseAuth.getInstance().currentUser?.uid

    // 🔥 Navegación por fin de juego
    LaunchedEffect(gameOver) {
        when (gameOver) {
            "WIN" -> onNavigateResult(true)
            "LOSE" -> onNavigateResult(false)
        }
    }

    // 🔥 Mostrar eventos
    LaunchedEffect(eventMessage) {
        if (eventMessage.isNotEmpty()) {
            Toast.makeText(context, eventMessage, Toast.LENGTH_SHORT).show()
        }
    }

    val me = state?.room?.players?.get(playerId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔹 INFO DEL JUEGO
        Text("Turno: ${state?.room?.currentTurn ?: 0} / ${state?.room?.maxTurns ?: 0}")
        Text("💰 $${me?.money ?: 0}", style = MaterialTheme.typography.headlineMedium)

        Text(actionResult, color = Color(0xFF4CAF50))

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 ACCIONES
        Button(onClick = {
            viewModel.onActionSelected(roomId, playerId, Action.SAVE)
        }) { Text("💾 Ahorrar") }

        Button(onClick = {
            viewModel.onActionSelected(roomId, playerId, Action.INVEST)
        }) { Text("📈 Invertir") }

        Button(onClick = {
            viewModel.onActionSelected(roomId, playerId, Action.SPEND)
        }) { Text("🛍 Gastar") }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔥 CHAT
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(messages) { msg ->

                val isMe = msg.senderId == myUserId

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {

                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(
                                if (isMe) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .padding(12.dp)
                    ) {

                        Text(
                            text = if (isMe) "Tú" else msg.senderName,
                            style = MaterialTheme.typography.labelSmall
                        )

                        Text(
                            text = msg.message
                        )
                    }
                }
            }
        }

        // 🔹 INPUT CHAT
        Row {
            TextField(
                value = chatInput,
                onValueChange = { chatInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...") }
            )

            Button(onClick = {
                if (chatInput.isNotEmpty()) {

                    val myUid = FirebaseAuth.getInstance().currentUser?.uid

                    val username = state?.room?.players?.get(myUid)?.name ?: "Jugador"

                    val msg = ChatMessage(
                        senderId = myUid ?: "",
                        senderName = username,
                        message = chatInput,
                        timestamp = System.currentTimeMillis()
                    )

                    viewModel.sendChatMessage(roomId, msg)
                    chatInput = ""
                }
            }) {
                Text("Enviar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.resetGame(roomId) }) {
            Text("🔄 Reiniciar")
        }
    }
}
