package com.datoban.rich_uncle.visual.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.datoban.rich_uncle.data.model.Action
import com.datoban.rich_uncle.data.model.ChatMessage
import com.datoban.rich_uncle.data.model.GameState
import com.datoban.rich_uncle.data.repository.ChatRepository
import com.datoban.rich_uncle.data.repository.GameRepository
import com.datoban.rich_uncle.domain.usecase.ApplyRandomEventUseCase
import com.datoban.rich_uncle.domain.usecase.CheckVictoryUseCase
import com.datoban.rich_uncle.domain.usecase.PerformActionUseCase
import com.google.firebase.auth.FirebaseAuth


class GameViewModel(
    private val gameRepo    : GameRepository,
    private val chatRepo    : ChatRepository,
    private val performAction : PerformActionUseCase,
    private val randomEvent   : ApplyRandomEventUseCase,
    private val checkVictory  : CheckVictoryUseCase
) : ViewModel() {

    private val _gameState = MutableLiveData<GameState>()
    val gameState: LiveData<GameState> = _gameState

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    val eventMessage = MutableLiveData<String>()
    val actionResult = MutableLiveData<String>()
    val gameOver     = MutableLiveData<String>() // "WIN" | "LOSE"

    fun loadRoom(roomId: String) {
        gameRepo.observeRoom(roomId).observeForever { room ->
            _gameState.value = GameState(room = room)
        }
        chatRepo.observeMessages(roomId).observeForever { msgs ->
            _messages.value = msgs
        }
    }

    fun onActionSelected(roomId: String, playerId: String, action: Action) {
        val state  = _gameState.value ?: return
        val room   = state.room
        val player = room.players[playerId]?.copy() ?: return

        // 1. Aplicar acción
        val (delta, resultMsg) = performAction.execute(action)
        player.money += delta
        player.lastAction = action.name
        player.lastResult = delta
        actionResult.value = resultMsg

        // 2. Evento aleatorio
        val (eventMsg, eventDelta) = randomEvent.execute()
        if (eventMsg.isNotEmpty()) {
            player.money += eventDelta
            player.lastResult += eventDelta
            eventMessage.value = eventMsg
        }

        // 3. Verificar eliminación
        if (player.money <= 0) {
            player.alive = false
            gameRepo.updatePlayer(roomId, player)
            gameOver.value = "LOSE"
            return
        }

        // 4. Verificar victoria (usa turno global)
        if (room.currentTurn >= room.maxTurns) {
            gameRepo.updatePlayer(roomId, player)
            gameOver.value = "WIN"
            return
        }

        // 5. Guardar jugador
        gameRepo.updatePlayer(roomId, player)

        // 6. Avanzar turno global
        gameRepo.advanceTurn(roomId, room.currentTurn + 1)
    }

    fun sendChatMessage(roomId: String, message: ChatMessage) {
        chatRepo.sendMessage(roomId, message)
    }



    fun resetGame(roomId: String) {
        gameRepo.resetRoom(roomId)
        gameOver.value = ""
        eventMessage.value = ""
        actionResult.value = ""
    }
}