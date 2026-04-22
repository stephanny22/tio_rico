package com.datoban.rich_uncle.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Player(
    val id: String = "",
    val name: String = "",
    var money: Int = 1000,
    var isAlive: Boolean = true,
    var currentTurn: Int = 0
)