package com.datoban.rich_uncle.data.model

data class GameState(
    val room: GameRoom = GameRoom(),
    val currentEvent: String? = null,
    val lastActionResult: String? = null
)