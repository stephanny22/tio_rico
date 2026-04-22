package com.datoban.rich_uncle.domain.usecase

import com.datoban.rich_uncle.data.model.GameRoom
import com.datoban.rich_uncle.data.model.Player

class CheckVictoryUseCase {

    fun isEliminated(player: Player): Boolean {
        return player.money <= 0
    }

    fun isWinner(room: GameRoom, player: Player): Boolean {
        return player.alive && room.currentTurn >= room.maxTurns
    }
}