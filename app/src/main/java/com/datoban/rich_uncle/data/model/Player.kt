package com.datoban.rich_uncle.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Player(
    var id: String = "",
    var name: String = "",
    var money: Int = 0,
    var alive: Boolean = true,
    var lastAction: String = "",
    var lastResult: Int = 0
)