package com.datoban.rich_uncle.data.model

data class GameState(
    val room: GameRoom
) {
    val players: Map<String, Player>
        get() = room.players

    val currentTurn: Int
        get() = room.currentTurn

    val maxTurns: Int
        get() = room.maxTurns
}