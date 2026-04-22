package com.datoban.rich_uncle.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties   // ← ignora campos que no conoce
data class GameRoom(
    val roomId: String = "",
    val players: Map<String, Player> = emptyMap(),
    val currentTurn: Int = 0,
    val maxTurns: Int = 10,
    val status: String = "WAITING"
)