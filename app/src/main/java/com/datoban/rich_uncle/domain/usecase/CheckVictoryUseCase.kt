package com.datoban.rich_uncle.domain.usecase

import com.datoban.rich_uncle.data.model.Player

class CheckVictoryUseCase {
    /** Retorna true si el jugador ganó, false si perdió */
    fun execute(player: Player, maxTurns: Int): Boolean {
        return player.money > 0 && player.currentTurn >= maxTurns
    }

    /** Retorna true si el jugador debe ser eliminado */
    fun isEliminated(player: Player): Boolean {
        return player.money <= 0
    }
}